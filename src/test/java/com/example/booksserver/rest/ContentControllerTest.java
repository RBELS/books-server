package com.example.booksserver.rest;

import com.example.booksserver.config.security.SecurityConfig;
import com.example.booksserver.dto.Author;
import com.example.booksserver.dto.Book;
import com.example.booksserver.dto.BookImage;
import com.example.booksserver.dto.Stock;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.service.impl.ContentService;
import com.example.booksserver.userstate.UserAuthor;
import com.example.booksserver.userstate.UserBaseFilters;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import com.example.booksserver.userstate.response.GetAuthorsResponse;
import com.example.booksserver.userstate.response.GetBooksResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContentController.class)
@Import(SecurityConfig.class)
class ContentControllerTest {
    @MockBean
    private JwtDecoder jwtDecoder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ContentService contentService;

    private final Book book;
    private final Author author;
    {
        author = new Author()
                .setId(1L)
                .setName("Author Name");
        book = new Book()
                .setId(10L)
                .setName("book name")
                .setPrice(new BigDecimal("10.00"))
                .setReleaseYear(2022)
                .setAuthors(Arrays.asList(
                        author
                ))
                .setStock(
                        new Stock()
                                .setId(2L)
                                .setAvailable(10)
                                .setOrdered(5)
                                .setInDelivery(2)
                )
                .setMainFile(
                        new BookImage()
                                .setId(3L)
                                .setType(ImageType.MAIN)
                                .setContent(new byte[1024])
                );
    }


    @Test
    void getBooks() throws Exception {
        Page<Book> page = new PageImpl<>(
                Arrays.asList(book, book, book),
                PageRequest.of(0, 3),
                9
        );
        when(contentService.getBooks(any(BooksFilters.class)))
                .thenReturn(page);

        String responseJson = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GetBooksResponse response = objectMapper.readValue(responseJson, GetBooksResponse.class);
    }

    @Test
    void getAuthors() throws Exception {
        Page<Author> page = new PageImpl<>(
                Arrays.asList(author, author, author),
                PageRequest.of(0, 3),
                9
        );
        when(contentService.getAuthors(any(AuthorsFilters.class)))
                .thenReturn(page);

        String responseJson = mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GetAuthorsResponse response = objectMapper.readValue(responseJson, GetAuthorsResponse.class);
    }

    @Test
    void getAllAuthors() throws Exception {
        List<Author> allAuthorsList = Arrays.asList(
                author, author, author
        );
        when(contentService.getAllAuthors())
                .thenReturn(allAuthorsList);
        String responseJson = mockMvc.perform(get("/authors/all"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserAuthor> response = Arrays.asList(
                objectMapper.readValue(responseJson, UserAuthor[].class)
        );
    }

    @Test
    void getBaseFilters() throws Exception {
        when(contentService.getMinMaxPrices())
                .thenReturn(Arrays.asList(
                        new BigDecimal("10.00"),
                        new BigDecimal("30.00")
                ));
        String responseJson = mockMvc.perform(get("/filterBaseInfo"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserBaseFilters response = objectMapper.readValue(responseJson, UserBaseFilters.class);
    }
}
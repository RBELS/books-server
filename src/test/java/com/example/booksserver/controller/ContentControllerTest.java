package com.example.booksserver.controller;

import com.example.booksserver.config.security.SecurityConfig;
import com.example.booksserver.model.service.Author;
import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.service.Stock;
import com.example.booksserver.service.impl.ContentServiceImpl;
import com.example.booksserver.model.dto.AuthorDTO;
import com.example.booksserver.model.dto.response.UserBaseFiltersResponse;
import com.example.booksserver.model.dto.filters.AuthorsFilters;
import com.example.booksserver.model.dto.filters.BooksFilters;
import com.example.booksserver.model.dto.response.GetAuthorsResponse;
import com.example.booksserver.model.dto.response.GetBooksResponse;
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
    private ContentServiceImpl contentService;

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

        List<AuthorDTO> response = Arrays.asList(
                objectMapper.readValue(responseJson, AuthorDTO[].class)
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

        UserBaseFiltersResponse response = objectMapper.readValue(responseJson, UserBaseFiltersResponse.class);
    }
}
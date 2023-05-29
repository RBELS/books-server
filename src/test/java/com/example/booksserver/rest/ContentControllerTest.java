package com.example.booksserver.rest;

import com.example.booksserver.config.security.SecurityConfig;
import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.dto.StockDTO;
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

    private final BookDTO bookDTO;
    private final AuthorDTO authorDTO;
    {
        authorDTO = new AuthorDTO()
                .setId(1L)
                .setName("Author Name");
        bookDTO = new BookDTO()
                .setId(10L)
                .setName("book name")
                .setPrice(new BigDecimal("10.00"))
                .setReleaseYear(2022)
                .setAuthors(Arrays.asList(
                        authorDTO
                ))
                .setStock(
                        new StockDTO()
                                .setId(2L)
                                .setAvailable(10)
                                .setOrdered(5)
                                .setInDelivery(2)
                )
                .setMainFile(
                        new BookImageDTO()
                                .setId(3L)
                                .setType(ImageType.MAIN)
                                .setContent(new byte[1024])
                );
    }


    @Test
    void getBooks() throws Exception {
        Page<BookDTO> page = new PageImpl<>(
                Arrays.asList(bookDTO, bookDTO, bookDTO),
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
        Page<AuthorDTO> page = new PageImpl<>(
                Arrays.asList(authorDTO, authorDTO, authorDTO),
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
        List<AuthorDTO> allAuthorsList = Arrays.asList(
                authorDTO, authorDTO, authorDTO
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
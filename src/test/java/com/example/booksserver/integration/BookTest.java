package com.example.booksserver.integration;


import com.example.booksserver.model.dto.BookDTO;
import com.example.booksserver.model.dto.filters.BooksFilters;
import com.example.booksserver.model.dto.request.PostBooksRequest;
import com.example.booksserver.model.dto.response.GetBooksResponse;
import com.example.booksserver.model.dto.response.PostAuthorsResponse;
import com.example.booksserver.model.dto.response.PostBooksResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookTest {
    @MockBean
    private JwtDecoder jwtDecoder;;

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private PostBooksResponse defaultBookResponse;
    private static final PostBooksRequest defaultRequest;
    static {
        defaultRequest = new PostBooksRequest()
                .setBookName("Book name")
                .setReleaseYear(2023)
                .setPrice(new BigDecimal("19.99"));
    }

    public static PostBooksResponse createBook(MockMvc mockMvc, ObjectMapper objectMapper, PostBooksRequest request) throws Exception {
        PostAuthorsResponse postAuthorsResponse = AuthorTest.createAuthor(mockMvc, objectMapper, "Author name");

        String createBookResponseStr = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request
                                .setAuthorIdList(Arrays.asList(postAuthorsResponse.getAuthor().getId())))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(createBookResponseStr, PostBooksResponse.class);
    }

    @BeforeEach
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.defaultBookResponse = createBook(mockMvc, objectMapper, defaultRequest);
    }

    @Test
    @WithMockUser
    public void createBookTest() throws Exception {
        PostBooksResponse postBooksResponse = BookTest.createBook(mockMvc, objectMapper, defaultRequest);
        BookDTO createdBookDTO = postBooksResponse.getBook();
        assertThat(defaultRequest.getBookName()).isEqualTo(createdBookDTO.getName());
        assertThat(defaultRequest.getPrice().compareTo(createdBookDTO.getPrice())).isEqualTo(0);
        assertThat(defaultRequest.getReleaseYear()).isEqualTo(createdBookDTO.getReleaseYear());
        assertThat(defaultRequest.getAuthorIdList().get(0)).isEqualTo(createdBookDTO.getAuthors().get(0).getId());
    }

    @Test
    public void getBooks() throws Exception {
        String getBooksResponseStr = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        GetBooksResponse getBooksResponse = objectMapper.readValue(getBooksResponseStr, GetBooksResponse.class);
        assertThat(getBooksResponse.getPage()).isEqualTo(1);
        assertThat(getBooksResponse.getCount()).isEqualTo(BooksFilters.DEFAULT_COUNT);
        assertThat(getBooksResponse.getTotalPages()).isEqualTo(1);
        assertThat(getBooksResponse.getContent()).hasSize(1);

        BookDTO bookDTO = getBooksResponse.getContent().get(0);
        assertThat(bookDTO.getName()).isEqualTo(defaultBookResponse.getBook().getName());
        assertThat(bookDTO.getPrice().compareTo(defaultBookResponse.getBook().getPrice())).isEqualTo(0);
        assertThat(bookDTO.getId()).isEqualTo(defaultBookResponse.getBook().getId());
        assertThat(bookDTO.getReleaseYear()).isEqualTo(defaultBookResponse.getBook().getReleaseYear());
        assertThat(bookDTO.getAvailable()).isEqualTo(defaultBookResponse.getBook().getAvailable());
        assertThat(bookDTO.getAuthors()).hasSize(1);
        assertThat(bookDTO.getAuthors().get(0)).isEqualTo(defaultBookResponse.getBook().getAuthors().get(0));
    }
}

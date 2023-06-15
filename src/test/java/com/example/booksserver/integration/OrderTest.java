package com.example.booksserver.integration;

import com.example.booksserver.model.service.CardInfo;
import com.example.booksserver.model.dto.request.PostAuthorsRequest;
import com.example.booksserver.model.dto.request.PostOrdersRequest;
import com.example.booksserver.model.dto.response.PostAuthorsResponse;
import com.example.booksserver.model.dto.response.PostBooksResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OrderTest {
    @MockBean
    private JwtDecoder jwtDecoder;;

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private Long authorId;
    private Long bookId;

    @BeforeEach
    @WithMockUser
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String createAuthorResponseStr = mockMvc
                .perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new PostAuthorsRequest()
                                        .setName("Author Name")
                        ))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(createAuthorResponseStr);
        PostAuthorsResponse createAuthorResponse = objectMapper.readValue(createAuthorResponseStr, PostAuthorsResponse.class);
        this.authorId = createAuthorResponse.getAuthor().getId();

        String createBookResponseStr = mockMvc
                .perform(multipart(HttpMethod.POST, "/books")
                        .file("mainImage", new byte[1024])
                        .param("authors", authorId.toString())
                        .param("name", "Book name")
                        .param("releaseYear", "2023")
                        .param("price", "10.00")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        PostBooksResponse createBookResponse = objectMapper.readValue(createBookResponseStr, PostBooksResponse.class);
    }

    @Test
    @WithMockUser
    public void createOrder() throws Exception {
        PostOrdersRequest requestBody = new PostOrdersRequest()
                .setInfo(new PostOrdersRequest.OrderInfo()
                        .setName("Order name")
                        .setPhone("Order phone")
                        .setEmail("Order email")
                        .setAddress("Order address")
                        .setBooks(
                                Arrays.asList(
                                        new PostOrdersRequest.OrdersBookDTO()
                                                .setId(bookId)
                                                .setCount(1)
                                )
                        )
                )
                .setCard(new CardInfo()
                        .setName("Holder name")
                        .setCvv("001")
                        .setNumber("1111222233334444")
                );

//        mockMvc.perform(post())
    }
}

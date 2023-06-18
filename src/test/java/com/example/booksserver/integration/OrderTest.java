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

    @BeforeEach
    @WithMockUser
    public void init() throws Exception {

    }

    @Test
    @WithMockUser
    public void createOrder() throws Exception {

    }
}

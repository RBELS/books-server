package com.example.booksserver.integration;

import com.example.booksserver.model.dto.AuthorDTO;
import com.example.booksserver.model.dto.filters.AuthorsFilters;
import com.example.booksserver.model.dto.request.PostAuthorsRequest;
import com.example.booksserver.model.dto.response.GetAuthorsResponse;
import com.example.booksserver.model.dto.response.PostAuthorsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorTest {
    @MockBean
    private JwtDecoder jwtDecoder;;

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webApplicationContext;

    public static PostAuthorsResponse createAuthor(MockMvc mockMvc, ObjectMapper objectMapper, String authorName) throws Exception {
        String createAuthorResponseStr = mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new PostAuthorsRequest()
                                        .setName(authorName)
                        )))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(createAuthorResponseStr, PostAuthorsResponse.class);
    }

    private final String initAuthorName = "Init Author Name";
    private long initAuthorId;

    @BeforeEach
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        PostAuthorsResponse postAuthorsResponse = createAuthor(mockMvc, objectMapper, initAuthorName);

        assertThat(postAuthorsResponse.getAuthor().getName()).isEqualTo(initAuthorName);
        initAuthorId = postAuthorsResponse.getAuthor().getId();
    }

    @Test
    @WithMockUser
    public void postAuthors() throws Exception {
        String authorName = "Author Name";
        PostAuthorsResponse createAuthorResponse = createAuthor(mockMvc, objectMapper, authorName);

        assertThat(createAuthorResponse.getStatus()).isEqualTo("SUCCESS");
        assertThat(createAuthorResponse.getAuthor().getName()).isEqualTo(authorName);
    }

    @Test
    public void getAuthors() throws Exception {
        String getAuthorsResponseStr = mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        GetAuthorsResponse getAuthorsResponse = objectMapper.readValue(getAuthorsResponseStr, GetAuthorsResponse.class);
        assertThat(getAuthorsResponse.getContent()).hasSize(1);

        AuthorDTO authorDTO = getAuthorsResponse.getContent().get(0);
        assertThat(authorDTO.getName()).isEqualTo(initAuthorName);
        assertThat(authorDTO.getId()).isEqualTo(initAuthorId);


        //default values
        assertThat(getAuthorsResponse.getPage()).isEqualTo(1);
        assertThat(getAuthorsResponse.getTotalPages()).isEqualTo(1);
        assertThat(getAuthorsResponse.getCount()).isEqualTo(AuthorsFilters.DEFAULT_COUNT);
    }
}

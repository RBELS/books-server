package com.example.booksserver.rest;

import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.config.security.SecurityConfig;
import com.example.booksserver.dto.BookImage;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.service.impl.FilesService;
import com.example.booksserver.userstate.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilesController.class)
@Import(SecurityConfig.class)
class FilesControllerTest {
    @MockBean
    private JwtDecoder jwtDecoder;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FilesService filesService;

    @Test
    void downloadImage() throws Exception {
        BookImage imageDTO = new BookImage()
                .setId(10L)
                .setType(ImageType.MAIN)
                .setContent(new byte[1024]);
        ResponseBodyException mockException = new ResponseBodyException(HttpStatus.NOT_FOUND, mock(ErrorResponse.class));

        when(filesService.getImageById(20L))
                .thenThrow(mockException);
        when(filesService.getImageById(10L))
                .thenReturn(imageDTO);


        mockMvc.perform(get("/static/image/10"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/static/image/20"))
                .andExpect(status().isNotFound());
    }
}
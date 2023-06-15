package com.example.booksserver.service.impl;

import com.example.booksserver.exception.ErrorResponseFactory;
import com.example.booksserver.exception.InternalErrorCode;
import com.example.booksserver.exception.ResponseBodyException;
import com.example.booksserver.model.service.BookImage;
import com.example.booksserver.model.entity.BookEntity;
import com.example.booksserver.model.entity.BookImageEntity;
import com.example.booksserver.model.entity.ImageType;
import com.example.booksserver.map.ImageMapper;
import com.example.booksserver.repository.BookImageRepository;
import com.example.booksserver.model.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class FilesServiceTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Mock
    private ImageMapper imageMapper;
    @Mock
    private BookImageRepository bookImageRepository;
    @Mock
    private ErrorResponseFactory errorResponseFactory;
    @InjectMocks
    private FilesServiceImpl filesService;

    @Test
    public void test() {
        BookImageEntity entity = new BookImageEntity();
        entity.setId(1L);
        entity.setType(ImageType.MAIN);
        entity.setBook(mock(BookEntity.class));
        entity.setContent(new byte[1024]);

        when(bookImageRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(imageMapper.entityToDto(any(BookImageEntity.class))).thenReturn(mock(BookImage.class));
        when(errorResponseFactory.create(any(HttpStatus.class), any(InternalErrorCode.class)))
                .thenReturn(mock(ErrorResponse.class));

        // TODO: Decide which library I should use?
        assertDoesNotThrow(() -> filesService.getImageById(1L));
        assertThatThrownBy(() -> filesService.getImageById(2L))
                .isInstanceOf(ResponseBodyException.class);
    }
}
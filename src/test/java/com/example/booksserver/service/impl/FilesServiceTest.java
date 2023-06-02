package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.ResponseStatusWithBodyExceptionFactory;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.BookImage;
import com.example.booksserver.entity.BookEntity;
import com.example.booksserver.entity.image.BookImageEntity;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.map.ImageMapper;
import com.example.booksserver.repository.BookImageRepository;
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
    private ResponseStatusWithBodyExceptionFactory exceptionFactory;
    @InjectMocks
    private FilesService filesService;

    @Test
    public void test() {
        BookImageEntity entity = new BookImageEntity();
        entity.setId(1L);
        entity.setType(ImageType.MAIN);
        entity.setBook(mock(BookEntity.class));
        entity.setContent(new byte[1024]);

        when(bookImageRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(imageMapper.entityToDto(any(BookImageEntity.class))).thenReturn(mock(BookImage.class));
        when(exceptionFactory.create(any(HttpStatus.class), any(ErrorResponseFactory.InternalErrorCode.class)))
                .thenReturn(mock(ResponseBodyException.class));

        // TODO: Decide which library I should use?
        assertDoesNotThrow(() -> filesService.getImageById(1L));
        assertThatThrownBy(() -> filesService.getImageById(2L))
                .isInstanceOf(ResponseBodyException.class);
    }
}
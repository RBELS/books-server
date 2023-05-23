package com.example.booksserver.service;

import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.map.ImageMapper;
import com.example.booksserver.repository.BookImageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilesServiceTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Mock
    private ImageMapper imageMapper;
    @Mock
    private BookImageRepository bookImageRepository;
    @InjectMocks
    private FilesService filesService;

    @Test
    public void test() {
        BookImage entity = new BookImage();
        entity.setId(1L);
        entity.setType(ImageType.MAIN);
        entity.setBook(mock(Book.class));
        entity.setContent(new byte[1024]);

        when(bookImageRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(imageMapper.entityToDto(any(BookImage.class))).thenReturn(mock(BookImageDTO.class));

        assertDoesNotThrow(() -> filesService.getImageById(1L));
        assertThrows(ResponseStatusException.class, () -> filesService.getImageById(2L));

    }
}

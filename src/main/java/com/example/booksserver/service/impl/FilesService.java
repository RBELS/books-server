package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.ResponseStatusWithBodyExceptionFactory;
import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.map.ImageMapper;
import com.example.booksserver.repository.BookImageRepository;
import com.example.booksserver.service.IFilesService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class FilesService implements IFilesService {
    private final BookImageRepository bookImageRepository;
    private final ImageMapper imageMapper;
    private final ResponseStatusWithBodyExceptionFactory exceptionFactory;

    public FilesService(
            BookImageRepository bookImageRepository,
            ImageMapper imageMapper,
            ResponseStatusWithBodyExceptionFactory exceptionFactory
    ) {
        this.bookImageRepository = bookImageRepository;
        this.imageMapper = imageMapper;
        this.exceptionFactory = exceptionFactory;
    }

    @Override
    public BookImageDTO getImageById(Long imageId) throws ResponseStatusException {
        Optional<BookImage> imageEntityOptional = bookImageRepository.findById(imageId);
        BookImage resultEntity = imageEntityOptional
                .orElseThrow(() -> exceptionFactory.create(HttpStatus.NOT_FOUND, ErrorResponseFactory.InternalErrorCode.IMAGE_NOT_FOUND));
        return imageMapper.entityToDto(resultEntity);
    }
}

package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.InternalErrorCode;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.BookImage;
import com.example.booksserver.map.ImageMapper;
import com.example.booksserver.repository.BookImageRepository;
import com.example.booksserver.service.IFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FilesService implements IFilesService {
    private final BookImageRepository bookImageRepository;
    private final ImageMapper imageMapper;
    private final ErrorResponseFactory errorResponseFactory;

    @Override
    public BookImage getImageById(Long imageId) throws ResponseStatusException {
        return bookImageRepository.findById(imageId)
                .map(imageMapper::entityToDto)
                .orElseThrow(() -> {
                    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                    return new ResponseBodyException(status,
                            errorResponseFactory.create(status, InternalErrorCode.IMAGE_NOT_FOUND)
                    );
                });
    }
}

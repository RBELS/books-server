package com.example.booksserver.service;

import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.map.ImageMapper;
import com.example.booksserver.repository.BookImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class FilesService implements IFilesService {
    private final BookImageRepository bookImageRepository;
    private final ImageMapper imageMapper;

    public FilesService(BookImageRepository bookImageRepository, ImageMapper imageMapper) {
        this.bookImageRepository = bookImageRepository;
        this.imageMapper = imageMapper;
    }

    @Override
    public BookImageDTO getImageById(Long imageId) throws ResponseStatusException {
        Optional<BookImage> imageEntityOpt = bookImageRepository.findById(imageId);
        if (imageEntityOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        BookImage imageEntity = imageEntityOpt.get();
        return imageMapper.entityToDto(imageEntity);
    }
}

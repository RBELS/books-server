package com.example.booksserver.service;

import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.map.ImageMapper;
import com.example.booksserver.repository.BookImageRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaticService implements IStaticService {
    private final BookImageRepository bookImageRepository;
    private final ImageMapper imageMapper;

    public StaticService(BookImageRepository bookImageRepository, ImageMapper imageMapper) {
        this.bookImageRepository = bookImageRepository;
        this.imageMapper = imageMapper;
    }

    @Override
    public BookImageDTO getImageById(Long imageId) {
        Optional<BookImage> imageEntityOpt = bookImageRepository.findById(imageId);
        if (imageEntityOpt.isEmpty()) {
            return null;
        }

        BookImage imageEntity = imageEntityOpt.get();
        return imageMapper.entityToDto(imageEntity);
    }
}

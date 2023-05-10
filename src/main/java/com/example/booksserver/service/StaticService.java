package com.example.booksserver.service;

import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.repository.BookImageRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaticService implements IStaticService {
    private final BookImageRepository bookImageRepository;

    public StaticService(BookImageRepository bookImageRepository) {
        this.bookImageRepository = bookImageRepository;
    }

    @Override
    public BookImageDTO getImageById(Long imageId) {
        Optional<BookImage> imageEntityOpt = bookImageRepository.findById(imageId);
        if (imageEntityOpt.isEmpty()) {
            return null;
        }

        BookImage imageEntity = imageEntityOpt.get();
        return new BookImageDTO(imageEntity.getId(), imageEntity.getType(), imageEntity.getContent());
    }
}

package com.example.booksserver.service;

import com.example.booksserver.model.service.BookImage;

public interface FilesService {
    BookImage getImageById(Long imageId);
}

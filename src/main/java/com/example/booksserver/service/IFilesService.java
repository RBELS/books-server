package com.example.booksserver.service;

import com.example.booksserver.dto.BookImage;

public interface IFilesService {
    BookImage getImageById(Long imageId);
}

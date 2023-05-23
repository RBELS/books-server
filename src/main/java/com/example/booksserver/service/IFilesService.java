package com.example.booksserver.service;

import com.example.booksserver.dto.BookImageDTO;

public interface IFilesService {
    BookImageDTO getImageById(Long imageId);
}

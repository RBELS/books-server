package com.example.booksserver.service;

import com.example.booksserver.dto.BookImageDTO;

public interface IStaticService {
    BookImageDTO getImageById(Long imageId);
}

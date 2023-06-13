package com.example.booksserver.controller;

import com.example.booksserver.model.service.BookImage;
import com.example.booksserver.service.FilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/static")
@RequiredArgsConstructor
public class FilesController {
    private final FilesService filesService;

    @GetMapping(value = "/image/{imageId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public Resource downloadImage(
            @PathVariable Long imageId
    ) {
        BookImage imageDTO = filesService.getImageById(imageId);
        byte[] imageBytes = imageDTO.getContent();
        return new ByteArrayResource(imageBytes);
    }
}

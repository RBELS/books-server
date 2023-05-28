package com.example.booksserver.rest;

import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.service.IFilesService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/static")
public class FilesController {

    private final IFilesService staticService;

    public FilesController(IFilesService staticService) {
        this.staticService = staticService;
    }

    @GetMapping(value = "/image/{imageId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    private Resource downloadImage(
            @PathVariable Long imageId
    ) {
        BookImageDTO imageDTO = staticService.getImageById(imageId);
        byte[] imageBytes = imageDTO.getContent();
        return new ByteArrayResource(imageBytes);
    }
}

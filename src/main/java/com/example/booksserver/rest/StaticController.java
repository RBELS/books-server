package com.example.booksserver.rest;

import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.service.IStaticService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
@RequestMapping("/static")
public class StaticController {

    private final IStaticService staticService;

    public StaticController(IStaticService staticService) {
        this.staticService = staticService;
    }

    @GetMapping(value = "/image/{imageId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    private Resource downloadImage(
            @PathVariable Long imageId
    ) {
        BookImageDTO imageDTO = staticService.getImageById(imageId);
        if (Objects.isNull(imageDTO)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        byte[] imageBytes = imageDTO.getContent();
        return new ByteArrayResource(imageBytes);
    }
}

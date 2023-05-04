package com.example.booksserver.rest.admin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class CreationController {
    @PostMapping(value = "/books", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createBook(
            @RequestParam("authors") List<Long> authorIdList,
            @RequestParam("name") String bookName,
            @RequestParam("releaseYear") Integer releaseYear,
            @RequestParam("price") Double price,
            @RequestParam("mainImage") MultipartFile mainImageFile,
            @RequestParam(value = "images[]", required = false) List<MultipartFile> images
    ) {

    }
}

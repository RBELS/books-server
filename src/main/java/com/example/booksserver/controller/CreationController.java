package com.example.booksserver.controller;

import com.example.booksserver.model.dto.request.PostBooksRequest;
import com.example.booksserver.model.service.Author;
import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.service.Stock;
import com.example.booksserver.model.dto.request.PostAuthorsRequest;
import com.example.booksserver.service.ContentService;
import com.example.booksserver.model.dto.response.PostAuthorsResponse;
import com.example.booksserver.model.dto.response.PostBooksResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CreationController {
    private final ContentService contentService;


    @PostMapping(value = "/books", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostBooksResponse> createBook(
            @RequestBody PostBooksRequest request
            ) {
        List<Author> authorList = request.getAuthorIdList().stream().map(contentService::getAuthorById).toList();

        Book dto = new Book(
                null, request.getBookName(), request.getReleaseYear(), request.getPrice(), authorList,
                new Stock(null, 10, 0, 0)
        );
        dto = contentService.createBook(dto);

        return new ResponseEntity<>(new PostBooksResponse(dto), HttpStatus.OK);
    }

    @PostMapping(value = "/authors", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostAuthorsResponse> createAuthor(
            @RequestBody PostAuthorsRequest request
    ) {
        Author newAuthor = new Author(null, request.getName());
        Author createdAuthor = contentService.createAuthor(newAuthor);
        return new ResponseEntity<>(new PostAuthorsResponse(createdAuthor), HttpStatus.OK);
    }
}

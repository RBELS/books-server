package com.example.booksserver.controller;

import com.example.booksserver.config.AppConfig;
import com.example.booksserver.model.service.Author;
import com.example.booksserver.model.service.Book;
import com.example.booksserver.service.ContentService;
import com.example.booksserver.model.dto.AuthorDTO;
import com.example.booksserver.model.dto.filters.AuthorsFilters;
import com.example.booksserver.model.dto.filters.BooksFilters;
import com.example.booksserver.model.dto.response.UserBaseFiltersResponse;
import com.example.booksserver.model.dto.response.GetAuthorsResponse;
import com.example.booksserver.model.dto.response.GetBooksResponse;
import org.springframework.boot.convert.Delimiter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
public class ContentController {
    private final ContentService contentService;
    private final String baseImageUrl;

    public ContentController(ContentService contentService, AppConfig appConfig) {
        this.contentService = contentService;
        this.baseImageUrl = appConfig.getServerAddress() + "/static/image/";
    }

    @GetMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetBooksResponse getBooks(
            @Delimiter(",") @RequestParam(required = false) List<Long> authors,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count
    ) {
        BooksFilters filters = new BooksFilters(authors, minPrice, maxPrice, page, count);
        Page<Book> books = contentService.getBooks(filters);
        return new GetBooksResponse(filters, books, baseImageUrl);
    }

    @GetMapping(value = "/authors", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetAuthorsResponse getAuthors(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count
    ) {
        AuthorsFilters filters = new AuthorsFilters(page, count);
        Page<Author> authors = contentService.getAuthors(filters);
        return new GetAuthorsResponse(authors);
    }

    @GetMapping(value = "/authors/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AuthorDTO> getAllAuthors() {
        List<Author> dtoList = contentService.getAllAuthors();
        return dtoList.stream().map(AuthorDTO::new).toList();
    }

    // TODO: Add to OpenAPI spec.
    @GetMapping(value = "/filterBaseInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBaseFiltersResponse getBaseFilters() {
        return new UserBaseFiltersResponse(contentService.getMinMaxPrices());
    }
}

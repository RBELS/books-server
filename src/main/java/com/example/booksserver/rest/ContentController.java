package com.example.booksserver.rest;

import com.example.booksserver.config.AppConfig;
import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.fillingtest.Filling;
import com.example.booksserver.service.IContentService;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import com.example.booksserver.userstate.UserBaseFilters;
import com.example.booksserver.userstate.response.GetAuthorsResponse;
import com.example.booksserver.userstate.response.GetBooksResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.convert.Delimiter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ContentController {

    Logger logger = LoggerFactory.getLogger("ContentController");

    private final IContentService contentService;
    private final String baseImageUrl;

    public ContentController(IContentService contentService, AppConfig appConfig) {
        this.contentService = contentService;

        // TODO: HIDE THIS
        this.baseImageUrl = appConfig.getServerAddress() + "/static/image/";
    }

    @GetMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetBooksResponse getBooks(
            @Delimiter(",") @RequestParam(required = false) List<Long> authors,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count
    ) {
        BooksFilters filters = new BooksFilters(authors, minPrice, maxPrice, page, count);
        List<BookDTO> books = contentService.getBooks(filters);
        return new GetBooksResponse(filters, books, baseImageUrl);
    }

    @GetMapping(value = "/authors", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetAuthorsResponse getAuthors(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count
    ) {
        AuthorsFilters filters = new AuthorsFilters(page, count);
        List<AuthorDTO> authors = contentService.getAuthors(filters);
        return new GetAuthorsResponse(filters, authors);
    }

    @GetMapping(value = "/filterBaseInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBaseFilters getBaseFilters() {
        return new UserBaseFilters(contentService.getMinMaxPrices());
    }

}

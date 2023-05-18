package com.example.booksserver.rest;

import com.example.booksserver.config.AppConfig;
import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.fillingtest.Filling;
import com.example.booksserver.service.IContentService;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import com.example.booksserver.userstate.UserAuthor;
import com.example.booksserver.userstate.UserBaseFilters;
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
    private final Filling dbFillComponent;
    private final String baseImageUrl;

    public ContentController(IContentService contentService, Filling dbFillComponent, AppConfig appConfig) {
        this.contentService = contentService;
        this.dbFillComponent = dbFillComponent;

        // TODO: HIDE THIS
        this.baseImageUrl = appConfig.getServerAddress() + "/static/image/";
    }

    @GetMapping("/insert")
    public void insert() {
        logger.info("[TEST] Insert request");
        dbFillComponent.insertInitialData(200, 100);
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
    public Iterable<UserAuthor> getAuthors(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count
    ) {
        Iterable<AuthorDTO> authors = contentService.getAuthors(new AuthorsFilters(page, count));
        List<UserAuthor> userAuthorsList = new ArrayList<>();
        authors.forEach(author -> userAuthorsList.add(new UserAuthor(author)));
        return userAuthorsList;
    }

    @GetMapping(value = "/filterBaseInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBaseFilters getBaseFilters() {
        return new UserBaseFilters(contentService.getMinMaxPrices());
    }

}

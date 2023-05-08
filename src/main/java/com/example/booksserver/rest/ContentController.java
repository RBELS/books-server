package com.example.booksserver.rest;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.fillingtest.Filling;
import com.example.booksserver.service.ContentService;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import com.example.booksserver.userstate.UserAuthor;
import com.example.booksserver.userstate.UserBaseFilters;
import com.example.booksserver.userstate.UserBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ContentController {

    Logger logger = LoggerFactory.getLogger("ContentController");

    private final ContentService contentService;
    private final Filling dbFillComponent;

    public ContentController(ContentService contentService, Filling dbFillComponent) {
        this.contentService = contentService;
        this.dbFillComponent = dbFillComponent;
    }

    @GetMapping("/insert")
    public void insert() {
        logger.info("[TEST] Insert request");
        dbFillComponent.insertInitialData(200, 100);
    }

    @GetMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<UserBook> getBooks(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count
    ) {
        List<BookDTO> books = contentService.getBooks(new BooksFilters(authorId, minPrice, maxPrice, page, count));
        List<UserBook> userBooksList = new ArrayList<>();
        books.forEach(book -> userBooksList.add(new UserBook(book)));
        return userBooksList;
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

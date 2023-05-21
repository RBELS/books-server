package com.example.booksserver.userstate;

import com.example.booksserver.dto.BookDTO;
import lombok.Getter;


@Getter
public class UserBook {
    private final long id;
    private final String name;
    private final Iterable<UserAuthor> authors;
    private final double price;
    private final int releaseYear;
    private final int available;
    private final String mainImage;

    public UserBook(BookDTO book, String baseUrl) {
        this.id = book.getId();
        this.name = book.getName();
        this.authors = book.getAuthors().stream().map(UserAuthor::new).toList();
        this.price = book.getPrice() / 100.0;
        this.releaseYear = book.getReleaseYear();
        this.mainImage = baseUrl + book.getMainFile().getId();
        this.available = book.getStock().getAvailable();
    }
}

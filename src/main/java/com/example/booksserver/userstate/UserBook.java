package com.example.booksserver.userstate;

import com.example.booksserver.dto.BookDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
public class UserBook {
    private long id;
    private String name;
    private Iterable<UserAuthor> authors;
    private BigDecimal price;
    private int releaseYear;
    private int available;
    private String mainImage;

    public UserBook(BookDTO book, String baseUrl) {
        this.id = book.getId();
        this.name = book.getName();
        this.authors = book.getAuthors().stream().map(UserAuthor::new).toList();
        this.price = book.getPrice();
        this.releaseYear = book.getReleaseYear();
        this.mainImage = baseUrl + book.getMainFile().getId();
        this.available = book.getStock().getAvailable();
    }
}

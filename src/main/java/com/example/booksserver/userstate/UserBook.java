package com.example.booksserver.userstate;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import lombok.Getter;


@Getter
public class UserBook {
    private final long id;
    private final String name;
    private final Iterable<String> authors;
    private final double price;
    private final int releaseYear;

    // is not final because of constructor processing
    private String mainImage;

    public UserBook(BookDTO book) {
        this.id = book.getId();
        this.name = book.getBookName();
        this.authors = book.getAuthorList().stream().map(AuthorDTO::getName).toList();
        this.price = book.getPrice() / 100.0;
        this.releaseYear = book.getReleaseYear();

        // set images ??bad solution??
        // a single main image always exists for any book
        this.mainImage = book.getMainImageSrc();
    }
}

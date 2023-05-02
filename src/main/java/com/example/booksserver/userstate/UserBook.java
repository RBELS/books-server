package com.example.booksserver.userstate;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import lombok.Getter;

@Getter
public class UserBook {
    private final long id;
    private final String name;
    private final Iterable<String> authors;
    private final double price;
    private final int releaseYear;
    private final String imageSrc;

    public UserBook(long id, String name, Iterable<String> author, long price, int releaseYear, String imageSrc) {
        this.id = id;
        this.name = name;
        this.authors = author;
        this.price = price / 100.0;
        this.releaseYear = releaseYear;
        this.imageSrc = imageSrc;
    }

    public UserBook(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.authors = book.getAuthors().stream().map(Author::getName).toList();
        this.price = book.getPrice() / 100.0;
        this.releaseYear = book.getReleaseYear();
        this.imageSrc = book.getImageSrc();
    }
}

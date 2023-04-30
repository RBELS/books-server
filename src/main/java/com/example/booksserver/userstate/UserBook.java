package com.example.booksserver.userstate;

import com.example.booksserver.entity.Book;
import lombok.Getter;

@Getter
public class UserBook {
    private final long id;
    private final String name;
    private final String author;
    private final double price;
    private final int releaseYear;
    private final String imageSrc;

    public UserBook(long id, String name, String author, long price, int releaseYear, String imageSrc) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price / 100.0;
        this.releaseYear = releaseYear;
        this.imageSrc = imageSrc;
    }

    public UserBook(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.author = book.getAuthor().getName();
        this.price = book.getPrice() / 100.0;
        this.releaseYear = book.getReleaseYear();
        this.imageSrc = book.getImageSrc();
    }
}

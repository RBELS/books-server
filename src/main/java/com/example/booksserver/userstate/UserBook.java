package com.example.booksserver.userstate;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.entity.image.ImageType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserBook {
    private final long id;
    private final String name;
    private final Iterable<String> authors;
    private final double price;
    private final int releaseYear;

    // is not final because of constructor processing
    private String mainImage;

    public UserBook(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.authors = book.getAuthors().stream().map(Author::getName).toList();
        this.price = book.getPrice() / 100.0;
        this.releaseYear = book.getReleaseYear();

        // set images ??bad solution??
        // a single main image always exists for any book
        for (BookImage image : book.getImages()) {
            if (image.getType() == ImageType.MAIN) {
                this.mainImage = image.getSource();
                break;
            }
        }
    }
}

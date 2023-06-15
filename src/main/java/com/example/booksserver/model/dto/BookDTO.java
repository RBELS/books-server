package com.example.booksserver.model.dto;

import com.example.booksserver.model.service.Book;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
public class BookDTO {
    private long id;
    private String name;
    private Iterable<AuthorDTO> authors;
    private BigDecimal price;
    private int releaseYear;
    private int available;
    private String mainImage;

    public BookDTO(Book book, String baseUrl) {
        this.id = book.getId();
        this.name = book.getName();
        this.authors = book.getAuthors().stream().map(AuthorDTO::new).toList();
        this.price = book.getPrice();
        this.releaseYear = book.getReleaseYear();
        this.mainImage = baseUrl + book.getMainFile().getId();
        this.available = book.getStock().getAvailable();
    }
}

package com.example.booksserver.model.dto;

import com.example.booksserver.model.service.Book;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
public class BookDTO {
    private long id;
    private String name;
    private List<AuthorDTO> authors;
    private BigDecimal price;
    private int releaseYear;
    private int available;

    public BookDTO(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.authors = book.getAuthors().stream().map(AuthorDTO::new).toList();
        this.price = book.getPrice();
        this.releaseYear = book.getReleaseYear();
        this.available = book.getStock().getAvailable();
    }
}

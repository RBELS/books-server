package com.example.booksserver.model.service;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
@ToString
@Builder
public class Book {
    private Long id;
    @Builder.Default
    private String name = "";
    private Integer releaseYear;
    private BigDecimal price;
    private BookImage mainFile;
    @Builder.Default
    private List<BookImage> imagesFileList = new ArrayList<>();
    @Builder.Default
    private List<Author> authors = new ArrayList<>();
    private Stock stock;
}

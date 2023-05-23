package com.example.booksserver.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class BookDTO {
    private Long id;
    private String name = "";
    private int releaseYear;
    private double price;
    private BookImageDTO mainFile;
    private List<BookImageDTO> imagesFileList = new ArrayList<>();
    private List<AuthorDTO> authors = new ArrayList<>();
    private StockDTO stock;
}

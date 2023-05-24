package com.example.booksserver.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class BookDTO {
    private Long id;
    private String name = "";
    private Integer releaseYear;
    private BigDecimal price;
    private BookImageDTO mainFile;
    private List<BookImageDTO> imagesFileList = new ArrayList<>();
    private List<AuthorDTO> authors = new ArrayList<>();
    private StockDTO stock;
}

package com.example.booksserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class BookDTO {
    private Long id;
    private String bookName;
    private int releaseYear;
    private double price;
    private String mainImageSrc;
    private List<String> imagesSrcList = new ArrayList<>();
    private List<AuthorDTO> authorList = new ArrayList<>();
}

package com.example.booksserver.dto;

import java.util.List;

public class BookDTO {
    private List<Long> authorIdList;
    private String bookName;
    private int releaseYear;
    private double price;
    private String mainImageSrc;
    private List<String> imagesSrcList;
}

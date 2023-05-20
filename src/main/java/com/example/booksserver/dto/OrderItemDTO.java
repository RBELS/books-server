package com.example.booksserver.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long id;
    private BookDTO book;
    private Integer count;
    private Long price;
}

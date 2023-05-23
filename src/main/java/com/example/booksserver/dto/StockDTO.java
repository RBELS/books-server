package com.example.booksserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockDTO {
    private Long id;
    private Integer available;
    private Integer ordered;
    private Integer inDelivery;
}

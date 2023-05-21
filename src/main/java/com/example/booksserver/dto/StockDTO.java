package com.example.booksserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockDTO {
    private Long id;
    private Integer available = 0;
    private Integer ordered = 0;
    private Integer inDelivery = 0;
}

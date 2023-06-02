package com.example.booksserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Stock {
    private Long id;
    private Integer available;
    private Integer ordered;
    private Integer inDelivery;
}

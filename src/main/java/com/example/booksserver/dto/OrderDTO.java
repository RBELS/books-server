package com.example.booksserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String address;
    private String phone;
    private String name;
    private String email;
    @Builder.Default
    private List<OrderItemDTO> orderItems = new ArrayList<>();
    private Date dateCreated;
}

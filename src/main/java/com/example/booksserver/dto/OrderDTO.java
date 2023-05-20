package com.example.booksserver.dto;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String address;
    private String phone;
    private String name;
    private String email;
    private List<OrderItemDTO> orderItems;
    private Date dateCreated;
}

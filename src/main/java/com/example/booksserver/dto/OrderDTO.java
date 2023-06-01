package com.example.booksserver.dto;

import com.example.booksserver.entity.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OrderDTO {
    private Long id;
    private String address;
    private String phone;
    private String name;
    private String email;
    @Builder.Default
    private List<OrderItemDTO> orderItems = new ArrayList<>();
    private Date dateCreated;
    private OrderStatus status;
}

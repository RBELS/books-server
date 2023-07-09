package com.example.booksserver.model.service;

import com.example.booksserver.model.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Order {
    private Long id;
    private String address;
    private String phone;
    private String name;
    private String email;
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
    private LocalDateTime dateCreated;
    private OrderStatus status;
    private OrderCancelStatus orderCancelStatus;
}

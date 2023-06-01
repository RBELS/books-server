package com.example.booksserver.repository;

import com.example.booksserver.entity.order.Order;
import com.example.booksserver.entity.order.OrderStatus;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface OrderRepository extends ListCrudRepository<Order, Long> {
    List<Order> findAllByStatus(OrderStatus status);
}

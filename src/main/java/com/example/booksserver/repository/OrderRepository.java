package com.example.booksserver.repository;

import com.example.booksserver.entity.order.OrderEntity;
import com.example.booksserver.entity.order.OrderStatus;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface OrderRepository extends ListCrudRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatus(OrderStatus status);
}

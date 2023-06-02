package com.example.booksserver.repository;

import com.example.booksserver.entity.order.OrderEntity;
import com.example.booksserver.entity.order.OrderStatus;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends ListCrudRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatus(OrderStatus status);
}

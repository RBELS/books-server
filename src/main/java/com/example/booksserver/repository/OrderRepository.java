package com.example.booksserver.repository;

import com.example.booksserver.model.entity.OrderEntity;
import com.example.booksserver.model.entity.OrderStatus;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends ListCrudRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatusIn(List<OrderStatus> statusList);
    List<OrderEntity> findAllByStatusAndDateCreatedBetween(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);
}

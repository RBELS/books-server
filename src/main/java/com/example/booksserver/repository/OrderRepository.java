package com.example.booksserver.repository;

import com.example.booksserver.model.entity.OrderEntity;
import com.example.booksserver.model.entity.OrderStatus;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends ListCrudRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatusIn(List<OrderStatus> statusList);
    List<OrderEntity> findAllByStatus(OrderStatus orderStatus);

    List<OrderEntity> findAllByStatusAndDateCreatedAfter(OrderStatus status, LocalDateTime after);
    List<OrderEntity> findAllByStatusAndDateCreatedBefore(OrderStatus status, LocalDateTime before);
}

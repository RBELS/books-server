package com.example.booksserver.repository;

import com.example.booksserver.model.entity.CancelStatus;
import com.example.booksserver.model.entity.OrderEntity;
import com.example.booksserver.model.entity.OrderStatus;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends ListCrudRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatusIn(List<OrderStatus> statusList);
    List<OrderEntity> findAllByStatusAndDateCreatedBetween(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);
    Optional<OrderEntity> findByIdAndDateCreatedBefore(Long orderId, LocalDateTime dateBefore);

    List<OrderEntity> findDistinctByStatusInAndOrderCancelStatus_StatusInAndDateCreatedAfter(List<OrderStatus> orderStatusNotInList, List<CancelStatus> cancelStatusInList, LocalDateTime startDate);
    List<OrderEntity> findDistinctByStatusInAndOrderCancelStatus_StatusInAndOrderCancelStatus_DateRequestedBetween(List<OrderStatus> orderStatusNotInList, List<CancelStatus> cancelStatusInList, LocalDateTime startDate, LocalDateTime endDate);
}

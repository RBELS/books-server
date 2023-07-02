package com.example.booksserver.repository;

import com.example.booksserver.model.entity.OrderEntity;
import com.example.booksserver.model.entity.OrderStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends ListCrudRepository<OrderEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000") })
    @Query("select oe from OrderEntity oe where oe.id = :id")
    Optional<OrderEntity> findByIdLocked(@Param("id") long id);

    List<OrderEntity> findAllByStatusIn(List<OrderStatus> statusList);
    List<OrderEntity> findAllByStatus(OrderStatus orderStatus);

    List<OrderEntity> findAllByStatusAndDateCreatedAfter(OrderStatus status, LocalDateTime after);
    List<OrderEntity> findAllByStatusAndDateCreatedBefore(OrderStatus status, LocalDateTime before);
}

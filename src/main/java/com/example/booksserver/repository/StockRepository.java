package com.example.booksserver.repository;

import com.example.booksserver.model.entity.StockEntity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends ListCrudRepository<StockEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000") })
    @Query("select se from StockEntity se where se.id = :id")
    Optional<StockEntity> findByIdLocked(@Param("id") Long id);
}

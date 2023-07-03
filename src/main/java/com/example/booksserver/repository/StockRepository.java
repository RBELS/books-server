package com.example.booksserver.repository;

import com.example.booksserver.model.entity.StockEntity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends ListCrudRepository<StockEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"), @QueryHint(name = "jakarta.persistence.query.timeout", value = "5000") })
    @Query("select se from StockEntity se where se.id = :id")
    Optional<StockEntity> findByIdLocked(@Param("id") Long id);

    @Modifying
    @Query("update StockEntity se set se.available = se.available - :count, se.inDelivery = se.inDelivery + :count where  se.id = :id")
    void updateAvailableAndInDeliveryCountById(@Param("id") Long stockId, @Param("count") int count);
}

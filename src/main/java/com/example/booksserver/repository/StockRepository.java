package com.example.booksserver.repository;

import com.example.booksserver.model.entity.StockEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface StockRepository extends ListCrudRepository<StockEntity, Long> {
    @Modifying
    @Query("update StockEntity se set se.available = se.available - :count, se.inDelivery = se.inDelivery + :count where se.id = :id and se.available >= :count")
    Integer updateStockMoveAvailable(@Param("id") Long stockId, @Param("count") int count);

    @Modifying
    @Query("update StockEntity se set se.available = se.available + :count, se.inDelivery = se.inDelivery - :count where se.id = :id and se.inDelivery >= :count")
    Integer updateStockMoveInDelivery(@Param("id") Long stockId, @Param("count") int count);
}

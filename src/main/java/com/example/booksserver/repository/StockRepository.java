package com.example.booksserver.repository;

import com.example.booksserver.model.entity.StockEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends ListCrudRepository<StockEntity, Long> {
}

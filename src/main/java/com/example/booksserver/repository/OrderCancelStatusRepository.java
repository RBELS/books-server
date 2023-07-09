package com.example.booksserver.repository;

import com.example.booksserver.model.entity.OrderCancelStatusEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderCancelStatusRepository extends ListCrudRepository<OrderCancelStatusEntity, Long> {
}

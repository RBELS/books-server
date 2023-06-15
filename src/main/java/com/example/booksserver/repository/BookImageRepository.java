package com.example.booksserver.repository;

import com.example.booksserver.model.entity.BookImageEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookImageRepository extends ListCrudRepository<BookImageEntity, Long> {
}

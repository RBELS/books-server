package com.example.booksserver.repository;

import com.example.booksserver.model.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends PagingAndSortingRepository<BookEntity, Long>, ListCrudRepository<BookEntity, Long> {
    Page<BookEntity> findAllByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<BookEntity> findDistinctAllByAuthors_idInAndPriceBetween(List<Long> id, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<BookEntity> findDistinctByAuthors_idIn(List<Long> id, Pageable pageable);

    @Query("SELECT max(price) FROM BookEntity")
    Optional<BigDecimal> findMaxPrice();

    @Query("SELECT min(price) FROM BookEntity")
    Optional<BigDecimal> findMinPrice();
}

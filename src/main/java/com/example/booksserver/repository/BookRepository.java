package com.example.booksserver.repository;

import com.example.booksserver.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long>, ListCrudRepository<Book, Long> {
    @NonNull
    Page<Book> findAll(@NonNull Pageable pageable);
    Page<Book> findAllByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Book> findAllByAuthors_idInAndPriceBetween(List<Long> id, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Book> findAllByAuthors_idIn(List<Long> id, Pageable pageable);

    // TODO: replace query

    @Query("SELECT max(price) FROM Book")
    Optional<BigDecimal> findMaxPrice();

    // TODO: replace query
    @Query("SELECT min(price) FROM Book")
    Optional<BigDecimal> findMinPrice();
}

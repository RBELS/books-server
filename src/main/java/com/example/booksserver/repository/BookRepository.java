package com.example.booksserver.repository;

import com.example.booksserver.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long>, ListCrudRepository<Book, Long> {
    @NonNull
    Page<Book> findAll(@NonNull Pageable pageable);
    Page<Book> findAllByPriceBetween(long minPrice, long maxPrice, Pageable pageable);
    Page<Book> findAllByAuthors_idInAndPriceBetween(List<Long> id, long minPrice, long maxPrice, Pageable pageable);

    // TODO: replcae query
    @Query("SELECT max(price) FROM Book")
    Long findMaxPrice();

    // TODO: replcae query
    @Query("SELECT min(price) FROM Book")
    Long findMinPrice();
}

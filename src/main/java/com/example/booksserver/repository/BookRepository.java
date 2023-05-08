package com.example.booksserver.repository;

import com.example.booksserver.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long>, CrudRepository<Book, Long> {
    @NonNull
    Page<Book> findAll(@NonNull Pageable pageable);
    Page<Book> findAllByPriceBetween(long minPrice, long maxPrice, Pageable pageable);
    Page<Book> findAllByAuthors_idAndPriceBetween(long id, long minPrice, long maxPrice, Pageable pageable);

    @Query("SELECT max(price) FROM Book")
    Long findMaxPrice();

    @Query("SELECT min(price) FROM Book")
    Long findMinPrice();

}

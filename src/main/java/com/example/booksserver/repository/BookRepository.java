package com.example.booksserver.repository;

import com.example.booksserver.entity.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findAll();
    List<Book> findAllByPriceBetween(long minPrice, long maxPrice);
    List<Book> findAllByAuthor_idAndPriceBetween(long id, long minPrice, long maxPrice);

    @Query("SELECT min(price) FROM Book")
    Long findMinPrice();

    @Query("SELECT max(price) FROM Book")
    Long findMaxPrice();

}

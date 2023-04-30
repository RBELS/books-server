package com.example.booksserver.repository;

import com.example.booksserver.entity.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
    public Iterable<Book> findAll();
}

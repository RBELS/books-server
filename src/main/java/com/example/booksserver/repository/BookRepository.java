package com.example.booksserver.repository;

import com.example.booksserver.entity.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findAll();
    List<Book> findAllByAuthor_id(long id);
}

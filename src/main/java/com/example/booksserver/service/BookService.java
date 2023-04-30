package com.example.booksserver.service;

import com.example.booksserver.entity.Book;
import com.example.booksserver.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

}

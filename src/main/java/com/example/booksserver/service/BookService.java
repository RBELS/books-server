package com.example.booksserver.service;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Author> getAllAuthors() {
        List<Author> resultList = new ArrayList<>();
        Iterable<Author> allAuthors = authorRepository.findAll();
        allAuthors.forEach(resultList::add);
        return resultList;
    }
}

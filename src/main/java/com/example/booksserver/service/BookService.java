package com.example.booksserver.service;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.userstate.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    public List<Book> getBooks(Filters filters) {
        if (filters.getAuthorId() == null) {
            return bookRepository.findAll();
        }
        return bookRepository.findAllByAuthor_id(filters.getAuthorId());
    }

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Author> getAllAuthors() {
        List<Author> allAuthors = authorRepository.findAllByOrderByNameAsc();
        return allAuthors;
    }
}

package com.example.booksserver.service;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.userstate.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    private Logger logger = LoggerFactory.getLogger("BookService");

    public List<Book> getBooks(Filters filters) {
        long minPrice = filters.getMinPrice() == null ? bookRepository.findMinPrice() : filters.getMinPrice();
        long maxPrice = filters.getMaxPrice() == null ? bookRepository.findMaxPrice() : filters.getMaxPrice();

        if (filters.getAuthorId() == null) {
            return bookRepository.findAllByPriceBetween(minPrice, maxPrice);
        }

        return bookRepository.findAllByAuthor_idAndPriceBetween(filters.getAuthorId(), minPrice, maxPrice);
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

    public List<Double> getMinMaxPrices() {
        return Arrays.asList(bookRepository.findMinPrice() / 100.0, bookRepository.findMaxPrice() / 100.0);
    }
}

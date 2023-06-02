package com.example.booksserver.service;

import com.example.booksserver.dto.Author;
import com.example.booksserver.dto.Book;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import org.springframework.data.domain.Page;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

public interface IContentService {
    Page<Book> getBooks(BooksFilters filters);
    Page<Author> getAuthors(AuthorsFilters filters);
    List<Author> getAllAuthors();
    Author getAuthorById(Long authorId);
    Book getBookById(Long bookId);
    List<BigDecimal> getMinMaxPrices();
    Author createAuthor(Author newAuthor) throws ResponseStatusException;
    Book createBook(Book newBook) throws ResponseStatusException;

}

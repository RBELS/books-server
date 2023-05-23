package com.example.booksserver.service;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface IContentService {
    List<BookDTO> getBooks(BooksFilters filters);
    List<AuthorDTO> getAuthors(AuthorsFilters filters);
    AuthorDTO getAuthorById(Long authorId);
    BookDTO getBookById(Long bookId);
    List<Double> getMinMaxPrices();
    AuthorDTO createAuthor(AuthorDTO newAuthorDTO) throws ResponseStatusException;
    BookDTO createBook(BookDTO newBookDTO) throws ResponseStatusException;

}

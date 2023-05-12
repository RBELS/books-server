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
    List<Double> getMinMaxPrices();
    void createAuthor(AuthorDTO newAuthorDTO) throws ResponseStatusException;
    void createBook(BookDTO newBookDTO) throws ResponseStatusException;

}

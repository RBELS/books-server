package com.example.booksserver.service;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import org.springframework.data.domain.Page;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

public interface IContentService {
    Page<BookDTO> getBooks(BooksFilters filters);
    Page<AuthorDTO> getAuthors(AuthorsFilters filters);
    AuthorDTO getAuthorById(Long authorId);
    BookDTO getBookById(Long bookId);
    List<BigDecimal> getMinMaxPrices();
    AuthorDTO createAuthor(AuthorDTO newAuthorDTO) throws ResponseStatusException;
    BookDTO createBook(BookDTO newBookDTO) throws ResponseStatusException;

}

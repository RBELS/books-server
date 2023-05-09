package com.example.booksserver.service;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;

import java.util.List;

public interface IContentService {
    List<BookDTO> getBooks(BooksFilters filters);
    List<AuthorDTO> getAuthors(AuthorsFilters filters);
    AuthorDTO getAuthorById(Long authorId);
    List<Double> getMinMaxPrices();
    boolean createAuthor(AuthorDTO newAuthorDTO);
    boolean createBook(BookDTO newBookDTO);

}

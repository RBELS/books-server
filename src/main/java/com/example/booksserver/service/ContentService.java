package com.example.booksserver.service;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ContentService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    // Return DTO?
    public List<Book> getBooks(BooksFilters filters) {
        int page = Objects.isNull(filters.getPage()) ? 0 : filters.getPage();
        int count = Objects.isNull(filters.getCount()) ? BooksFilters.DEFAULT_COUNT_PER_PAGE : filters.getCount();
        long minPrice = filters.getMinPrice() == null ? bookRepository.findMinPrice() : filters.getMinPrice();
        long maxPrice = filters.getMaxPrice() == null ? bookRepository.findMaxPrice() : filters.getMaxPrice();

        if (filters.getAuthorId() == null) {
            return bookRepository.findAllByPriceBetween(minPrice, maxPrice, PageRequest.of(page, count)).stream().toList();
        }

        return bookRepository
                .findAllByAuthors_idAndPriceBetween(
                        filters.getAuthorId(),
                        minPrice, maxPrice,
                        PageRequest.of(page, count)
                ).getContent();
    }

    // TESTS ONLY
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    // TESTS ONLY
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    private final Sort authorsAscSort = Sort.by("name", "id").ascending();

    // Return DTO?
    public List<Author> getAuthors(AuthorsFilters filters) {
        if (Objects.isNull(filters.getPage()) || Objects.isNull(filters.getCount())) {
            return authorRepository.findAll(authorsAscSort);
        }
        return authorRepository.findAll(PageRequest.of(filters.getPage(), filters.getCount(), authorsAscSort)).getContent();
    }

    public List<Double> getMinMaxPrices() {
        return Arrays.asList(bookRepository.findMinPrice() / 100.0, bookRepository.findMaxPrice() / 100.0);
    }

    // Do i need to pass an object here? (DTO)
    public void createAuthor(String name) {
        Author authorEntity = new Author();
        authorEntity.setName(name);
        authorRepository.save(authorEntity);
    }

//    public void createBook()
}

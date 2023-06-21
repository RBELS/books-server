package com.example.booksserver.service.impl;

import com.example.booksserver.exception.ErrorResponseFactory;
import com.example.booksserver.exception.InternalErrorCode;
import com.example.booksserver.exception.ResponseBodyException;
import com.example.booksserver.model.service.Author;
import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.entity.AuthorEntity;
import com.example.booksserver.model.entity.BookEntity;
import com.example.booksserver.map.AuthorMapper;
import com.example.booksserver.map.BookMapper;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.service.ContentService;
import com.example.booksserver.model.dto.filters.AuthorsFilters;
import com.example.booksserver.model.dto.filters.BooksFilters;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;
    private final ErrorResponseFactory errorResponseFactory;

//    @Transactional
    public Page<Book> getBooks(BooksFilters filters) {
        int page = Objects.isNull(filters.getPage()) ? 0 : filters.getPage();
        int count = Objects.isNull(filters.getCount()) ? BooksFilters.DEFAULT_COUNT : filters.getCount();
        PageRequest pageRequest = PageRequest.of(page, count);

        Page<BookEntity> entityPage;
        if (!Objects.isNull(filters.getMinPrice()) && !Objects.isNull(filters.getMaxPrice())) {
            if (filters.getAuthorIdList().isEmpty()) {
                entityPage = bookRepository.findAllByPriceBetween(filters.getMinPrice(), filters.getMaxPrice(), pageRequest);
            } else {
                entityPage = bookRepository
                        .findDistinctAllByAuthors_idInAndPriceBetween(
                                filters.getAuthorIdList(),
                                filters.getMinPrice(), filters.getMaxPrice(),
                                pageRequest
                        );
            }
        } else {
            if (filters.getAuthorIdList().isEmpty()) {
                entityPage = bookRepository.findAll(pageRequest);
            } else {
                entityPage = bookRepository.findDistinctByAuthors_idIn(filters.getAuthorIdList(), pageRequest);
            }
        }

        return bookMapper.entityToServicePage(entityPage);
    }

    private static final Sort AUTHORS_ASC_SORT = Sort.by("name", "id").ascending();

    public Page<Author> getAuthors(AuthorsFilters filters) {
        Page<AuthorEntity> entityPage = authorRepository.findAll(
                PageRequest.of(filters.getPage(), filters.getCount(), AUTHORS_ASC_SORT)
        );
        return authorMapper.entityToServicePage(entityPage);
    }

    public List<Author> getAllAuthors() {
        List<AuthorEntity> entityList = authorRepository.findAll(AUTHORS_ASC_SORT);
        return authorMapper.entityToService(entityList);
    }

    public Author getAuthorById(Long authorId) {
        Optional<AuthorEntity> entity = authorRepository.findById(authorId);
        return authorMapper.entityToService(entity.orElse(null));
    }

//    @Transactional
    public Book getBookById(Long bookId) {
        Optional<BookEntity> entity = bookRepository.findById(bookId);
        return bookMapper.entityToService(entity.orElse(null));
    }

    public List<BigDecimal> getMinMaxPrices() {
        return Arrays.asList(bookRepository.findMinPrice().orElse(null), bookRepository.findMaxPrice().orElse(null));
    }

    private void validateAuthor(Author author) throws ResponseStatusException {
        if (author.getName().isBlank()) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw  new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.AUTHOR_BAD_NAME)
            );
        }
    }

    public Author createAuthor(Author newAuthor) throws ResponseStatusException {
        validateAuthor(newAuthor);
        AuthorEntity authorEntity = authorMapper.serviceToEntity(newAuthor);
        authorEntity = authorRepository.save(authorEntity);
        return authorMapper.entityToService(authorEntity);
    }

    private void validateBook(Book book) throws ResponseStatusException {
        if (book.getName().isBlank()) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.BOOK_BAD_NAME)
            );
        } else if (book.getAuthors().isEmpty() || book.getAuthors().contains(null)) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.BOOK_BAD_AUTHORS)
            );
        } else if (book.getReleaseYear() < 0) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.BOOK_BAD_RELEASE_YEAR)
            );
        } else if (book.getPrice().doubleValue() < 0) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.BOOK_BAD_PRICE)
            );
        }
    }

    public Book createBook(Book newBook) throws ResponseStatusException {
        validateBook(newBook);
        BookEntity entity = bookMapper.serviceToEntity(newBook);
        entity = bookRepository.save(entity);
        return bookMapper.entityToService(entity);
    }
}

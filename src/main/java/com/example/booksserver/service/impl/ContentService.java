package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.ResponseStatusWithBodyExceptionFactory;
import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.map.AuthorMapper;
import com.example.booksserver.map.BookMapper;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.service.IContentService;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
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
public class ContentService implements IContentService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;
    private final ResponseStatusWithBodyExceptionFactory exceptionFactory;

    @Transactional
    public Page<BookDTO> getBooks(BooksFilters filters) {
        int page = Objects.isNull(filters.getPage()) ? 0 : filters.getPage();
        int count = Objects.isNull(filters.getCount()) ? BooksFilters.DEFAULT_COUNT_PER_PAGE : filters.getCount();
        PageRequest pageRequest = PageRequest.of(page, count);

        Page<Book> entityPage;
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

        return bookMapper.entityToDtoPage(entityPage);
    }

    private static final Sort AUTHORS_ASC_SORT = Sort.by("name", "id").ascending();

    public Page<AuthorDTO> getAuthors(AuthorsFilters filters) {
        Page<Author> entityPage = authorRepository.findAll(
                PageRequest.of(filters.getPage(), filters.getCount(), AUTHORS_ASC_SORT)
        );
        return authorMapper.entityToDtoPage(entityPage);
    }

    public List<AuthorDTO> getAllAuthors() {
        List<Author> entityList = authorRepository.findAll(AUTHORS_ASC_SORT);
        return authorMapper.entityToDto(entityList);
    }

    public AuthorDTO getAuthorById(Long authorId) {
        Optional<Author> entity = authorRepository.findById(authorId);
        return authorMapper.entityToDto(entity.orElse(null));
    }

    @Transactional
    public BookDTO getBookById(Long bookId) {
        Optional<Book> entity = bookRepository.findById(bookId);
        return bookMapper.entityToDto(entity.orElse(null));
    }

    public List<BigDecimal> getMinMaxPrices() {
        return Arrays.asList(bookRepository.findMinPrice().orElse(null), bookRepository.findMaxPrice().orElse(null));
    }

    private void validateAuthor(AuthorDTO authorDTO) throws ResponseStatusException {
        if (authorDTO.getName().isBlank())
            throw exceptionFactory.create(
                    HttpStatus.BAD_REQUEST,
                    ErrorResponseFactory.InternalErrorCode.AUTHOR_BAD_NAME
            );
    }

    public AuthorDTO createAuthor(AuthorDTO newAuthorDTO) throws ResponseStatusException {
        validateAuthor(newAuthorDTO);
        Author authorEntity = authorMapper.dtoToEntity(newAuthorDTO);
        authorEntity = authorRepository.save(authorEntity);
        return authorMapper.entityToDto(authorEntity);
    }

    private void validateBook(BookDTO bookDTO) throws ResponseStatusException {
        if (bookDTO.getName().isBlank()) {
           throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.BOOK_BAD_NAME);
        } else if (bookDTO.getAuthors().isEmpty() || bookDTO.getAuthors().contains(null)) {
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.BOOK_BAD_AUTHORS);
        } else if (bookDTO.getReleaseYear() < 0) {
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.BOOK_BAD_RELEASE_YEAR);
        } else if (bookDTO.getPrice().doubleValue() < 0) {
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.BOOK_BAD_PRICE);
        }
    }

    public BookDTO createBook(BookDTO newBookDTO) throws ResponseStatusException {
        validateBook(newBookDTO);
        Book entity = bookMapper.dtoToEntity(newBookDTO);
        entity = bookRepository.save(entity);
        return bookMapper.entityToDto(entity);
    }
}

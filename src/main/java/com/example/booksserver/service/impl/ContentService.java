package com.example.booksserver.service.impl;

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
public class ContentService implements IContentService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

    public ContentService(BookRepository bookRepository, AuthorRepository authorRepository, AuthorMapper authorMapper, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.bookMapper = bookMapper;
    }


    // TODO: Return Page, not List
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
                        .findAllByAuthors_idInAndPriceBetween(
                                filters.getAuthorIdList(),
                                filters.getMinPrice(), filters.getMaxPrice(),
                                pageRequest
                        );
            }
        } else {
            if (filters.getAuthorIdList().isEmpty()) {
                entityPage = bookRepository.findAll(pageRequest);
            } else {
                entityPage = bookRepository.findAllByAuthors_idIn(filters.getAuthorIdList(), pageRequest);
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
        if (authorDTO.getName().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public AuthorDTO createAuthor(AuthorDTO newAuthorDTO) throws ResponseStatusException {
        validateAuthor(newAuthorDTO);
        Author authorEntity = authorMapper.dtoToEntity(newAuthorDTO);
        authorEntity = authorRepository.save(authorEntity);
        return authorMapper.entityToDto(authorEntity);
    }

    private void validateBook(BookDTO bookDTO) throws ResponseStatusException {
        if (
                bookDTO.getName().isBlank() || bookDTO.getAuthors().isEmpty()
                || bookDTO.getReleaseYear() < 0 || bookDTO.getPrice().doubleValue() < 0
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public BookDTO createBook(BookDTO newBookDTO) throws ResponseStatusException {
        validateBook(newBookDTO);
        Book entity = bookMapper.dtoToEntity(newBookDTO);
        entity = bookRepository.save(entity);
        return bookMapper.entityToDto(entity);
    }
}

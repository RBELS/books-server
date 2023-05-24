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
    public List<BookDTO> getBooks(BooksFilters filters) {
        int page = Objects.isNull(filters.getPage()) ? 0 : filters.getPage();
        int count = Objects.isNull(filters.getCount()) ? BooksFilters.DEFAULT_COUNT_PER_PAGE : filters.getCount();
        // TODO:

        long minPrice = filters.getMinPrice() == null ? bookRepository.findMinPrice() : filters.getMinPrice();
        long maxPrice = filters.getMaxPrice() == null ? bookRepository.findMaxPrice() : filters.getMaxPrice();

        Page<Book> entityPage;
        if (filters.getAuthorIdList().isEmpty()) {
            entityPage = bookRepository.findAllByPriceBetween(minPrice, maxPrice, PageRequest.of(page, count));
        } else {
            entityPage = bookRepository
                    .findAllByAuthors_idInAndPriceBetween(
                            filters.getAuthorIdList(),
                            minPrice, maxPrice,
                            PageRequest.of(page, count)
                    );
        }

        filters.setOutTotalPages(entityPage.getTotalPages());
        return bookMapper.entityToDto(entityPage.getContent());
    }

    // TESTS ONLY
    public void saveAuthor(Author author) {
        authorRepository.save(author);
    }

    // TESTS ONLY
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    private static final Sort AUTHORS_ASC_SORT = Sort.by("name", "id").ascending();

    public List<AuthorDTO> getAuthors(AuthorsFilters filters) {
        Page<Author> entityPage = authorRepository.findAll(
                PageRequest.of(filters.getPage(), filters.getCount(), AUTHORS_ASC_SORT)
        );
        filters.setOutTotalPages(entityPage.getTotalPages());
        return authorMapper.entityToDto(entityPage.getContent());
    }

    public AuthorDTO getAuthorById(Long authorId) {
        Optional<Author> entity = authorRepository.findById(authorId);
        AuthorDTO resultDTO = null;
        if (entity.isPresent()) {
            resultDTO = authorMapper.entityToDto(entity.get());
        }
        return resultDTO;
    }

    @Transactional
    public BookDTO getBookById(Long bookId) {
        Optional<Book> entity = bookRepository.findById(bookId);
        BookDTO resultDTO = null;
        if (entity.isPresent()) {
            resultDTO = bookMapper.entityToDto(entity.get());
        }
        return resultDTO;
    }

    public List<Double> getMinMaxPrices() {
        return Arrays.asList(bookRepository.findMinPrice() / 100.0, bookRepository.findMaxPrice() / 100.0);
    }

    private boolean isAuthorValid(AuthorDTO authorDTO) {
        return !authorDTO.getName().isBlank();
    }

    public AuthorDTO createAuthor(AuthorDTO newAuthorDTO) throws ResponseStatusException {
        boolean authorValid = isAuthorValid(newAuthorDTO);
        if (!authorValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Author authorEntity = authorMapper.dtoToEntity(newAuthorDTO);
        authorEntity = authorRepository.save(authorEntity);
        return authorMapper.entityToDto(authorEntity);
    }

    private boolean isBookValid(BookDTO bookDTO) {
        if (
                bookDTO.getName().isBlank() || bookDTO.getAuthors().isEmpty()
                || bookDTO.getReleaseYear() < 0 || bookDTO.getPrice().doubleValue() < 0
        ) {
            return false;
        }
        return true;
    }

    public BookDTO createBook(BookDTO newBookDTO) throws ResponseStatusException {
        if (!isBookValid(newBookDTO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Book entity = bookMapper.dtoToEntity(newBookDTO);
        entity = bookRepository.save(entity);
        return bookMapper.entityToDto(entity);
    }
}

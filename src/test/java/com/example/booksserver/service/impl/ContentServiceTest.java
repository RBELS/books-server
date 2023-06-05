package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.InternalErrorCode;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.Author;
import com.example.booksserver.dto.Book;
import com.example.booksserver.dto.BookImage;
import com.example.booksserver.dto.Stock;
import com.example.booksserver.entity.AuthorEntity;
import com.example.booksserver.entity.BookEntity;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.map.AuthorMapper;
import com.example.booksserver.map.BookMapper;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import com.example.booksserver.userstate.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ContentServiceTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private AuthorMapper authorMapper;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private ErrorResponseFactory errorResponseFactory;
    @InjectMocks
    private ContentService contentService;

    @Test
    void getBooks() {
        Page<BookEntity> mockEntityPage = mock(PageImpl.class);
        Page<Book> mockDtoPage = mock(PageImpl.class);

        when(bookRepository.findAllByPriceBetween(any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class)))
                .thenReturn(mockEntityPage);
        when(bookRepository.findDistinctAllByAuthors_idInAndPriceBetween(any(List.class), any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class)))
                .thenReturn(mockEntityPage);
        when(bookRepository.findDistinctByAuthors_idIn(any(List.class), any(Pageable.class)))
                .thenReturn(mockEntityPage);
        when(bookRepository.findAll(any(Pageable.class)))
                .thenReturn(mockEntityPage);

        when(bookMapper.entityToDtoPage(mockEntityPage))
                .thenReturn(mockDtoPage);

        List<Long> authorIdList = Arrays.asList(1L, 2L, 3L);
        BigDecimal minPrice = new BigDecimal("10.00"), maxPrice = new BigDecimal("20.00");

        BooksFilters filters = new BooksFilters(authorIdList, minPrice, maxPrice, 0, 20);
        Page<Book> dtoPage = contentService.getBooks(filters);
        assertThat(dtoPage).isNotNull();

        filters = new BooksFilters(new ArrayList<>(), minPrice, maxPrice, 0, 20);
        dtoPage = contentService.getBooks(filters);
        assertThat(dtoPage).isNotNull();

        filters = new BooksFilters(authorIdList, null, null, 0, 20);
        dtoPage = contentService.getBooks(filters);
        assertThat(dtoPage).isNotNull();

        filters = new BooksFilters(new ArrayList<>(), null, null, 0, 20);
        dtoPage = contentService.getBooks(filters);
        assertThat(dtoPage).isNotNull();
    }

    @Test
    void getAuthors() {
        Page<AuthorEntity> mockEntityPage = mock(PageImpl.class);
        Page<Author> mockDtoPage = mock(PageImpl.class);

        when(authorRepository.findAll(any(Pageable.class))).thenReturn(mockEntityPage);
        when(authorMapper.entityToDtoPage(mockEntityPage)).thenReturn(mockDtoPage);

        AuthorsFilters filters = new AuthorsFilters(0, 20);
        Page<Author> queriedPage = contentService.getAuthors(filters);
        assertThat(queriedPage).isNotNull();
    }

    @Test
    void getAllAuthors() {
        AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
        Author mockAuthor = mock(Author.class);

        List<AuthorEntity> entityList = Arrays.asList(mockAuthorEntity, mockAuthorEntity, mockAuthorEntity);
        List<Author> dtoList = Arrays.asList(mockAuthor, mockAuthor, mockAuthor);

        when(authorRepository.findAll(any(Sort.class))).thenReturn(entityList);
        when(authorMapper.entityToDto(entityList)).thenReturn(dtoList);

        List<Author> authors = contentService.getAllAuthors();
        assertThat(authors).hasSize(3);
    }

    @Test
    void getAuthorById() {
        AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
        Author mockAuthor = mock(Author.class);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(mockAuthorEntity));
        when(authorMapper.entityToDto(any(AuthorEntity.class))).thenReturn(mockAuthor);

        Author dto = contentService.getAuthorById(1L);
        assertThat(dto).isNotNull();

        dto = contentService.getAuthorById(2L);
        assertThat(dto).isNull();
    }

    @Test
    void getBookById() {
        BookEntity mockBookEntity = mock(BookEntity.class);
        Book mockBook = mock(Book.class);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBookEntity));
        when(bookMapper.entityToDto(any(BookEntity.class))).thenReturn(mockBook);

        Book dto = contentService.getBookById(1L);
        assertThat(dto).isNotNull();

        dto = contentService.getBookById(2L);
        assertThat(dto).isNull();
    }

    @Test
    void getMinMaxPrices() {
        BigDecimal minPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("20.00");
        when(bookRepository.findMinPrice()).thenReturn(Optional.of(minPrice));
        when(bookRepository.findMaxPrice()).thenReturn(Optional.of(maxPrice));

        List<BigDecimal> minMaxPrices = contentService.getMinMaxPrices();
        assertThat(minMaxPrices).containsExactly(minPrice, maxPrice);
    }

    @Test
    void createAuthor() {
        Author author = new Author(null, "Author name");
        Author savedAuthor = new Author(10L, "Author name");

        when(authorMapper.dtoToEntity(any(Author.class)))
                .thenReturn(mock(AuthorEntity.class));
        when(authorRepository.save(any(AuthorEntity.class)))
                .thenReturn(mock(AuthorEntity.class));
        when(authorMapper.entityToDto(any(AuthorEntity.class)))
                .thenReturn(savedAuthor);

        when(errorResponseFactory.create(any(HttpStatus.class), any(InternalErrorCode.class)))
                .thenReturn(mock(ErrorResponse.class));

        assertDoesNotThrow(() -> contentService.createAuthor(author));
        Author returnedAuthor = contentService.createAuthor(author);
        assertThat(returnedAuthor.getId()).isNotNull();

        author.setName("   ");
        assertThrows(ResponseBodyException.class, () -> contentService.createAuthor(author));

        author.setName("");
        assertThrows(ResponseBodyException.class, () -> contentService.createAuthor(author));
    }

    @Test
    void createBook() {
        Author someAuthor = new Author()
                .setId(20L)
                .setName("Author name");
        Book.BookBuilder builder = Book.builder()
                .id(null)
                .name("book name")
                .imagesFileList(new ArrayList<>())
                .price(new BigDecimal("100.00"))
                .releaseYear(2022)
                .stock(mock(Stock.class))
                .mainFile(new BookImage(10L, ImageType.MAIN, new byte[1024]))
                .authors(Arrays.asList(someAuthor));
        Book book = builder.build();
        Book savedBook = builder.id(30L).build();

        when(bookMapper.dtoToEntity(any(Book.class)))
                .thenReturn(mock(BookEntity.class));
        when(bookRepository.save(any(BookEntity.class)))
                .thenReturn(mock(BookEntity.class));
        when(bookMapper.entityToDto(any(BookEntity.class)))
                .thenReturn(savedBook);

        when(errorResponseFactory.create(any(HttpStatus.class), any(InternalErrorCode.class)))
                .thenReturn(mock(ErrorResponse.class));

        assertDoesNotThrow(() -> contentService.createBook(book));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                book.setName("    ")
        ));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                book.setName("")
        ));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                book.setName("book name").setPrice(new BigDecimal("-10.0"))
        ));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                book.setPrice(new BigDecimal("10.0")).setReleaseYear(-10)
        ));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                book.setReleaseYear(2022).setAuthors(Arrays.asList(someAuthor, null))
        ));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                book.setReleaseYear(2022).setAuthors(new ArrayList<>())
        ));
    }
}
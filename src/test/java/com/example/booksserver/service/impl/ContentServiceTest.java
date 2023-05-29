package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.ResponseStatusWithBodyExceptionFactory;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.dto.StockDTO;
import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.map.AuthorMapper;
import com.example.booksserver.map.BookMapper;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
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
    private ResponseStatusWithBodyExceptionFactory exceptionFactory;
    @InjectMocks
    private ContentService contentService;

    @Test
    void getBooks() {
        Page<Book> mockEntityPage = mock(PageImpl.class);
        Page<BookDTO> mockDtoPage = mock(PageImpl.class);

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
        Page<BookDTO> dtoPage = contentService.getBooks(filters);
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
        Page<Author> mockEntityPage = mock(PageImpl.class);
        Page<AuthorDTO> mockDtoPage = mock(PageImpl.class);

        when(authorRepository.findAll(any(Pageable.class))).thenReturn(mockEntityPage);
        when(authorMapper.entityToDtoPage(mockEntityPage)).thenReturn(mockDtoPage);

        AuthorsFilters filters = new AuthorsFilters(0, 20);
        Page<AuthorDTO> queriedPage = contentService.getAuthors(filters);
        assertThat(queriedPage).isNotNull();
    }

    @Test
    void getAllAuthors() {
        Author mockAuthorEntity = mock(Author.class);
        AuthorDTO mockAuthorDTO = mock(AuthorDTO.class);

        List<Author> entityList = Arrays.asList(mockAuthorEntity, mockAuthorEntity, mockAuthorEntity);
        List<AuthorDTO> dtoList = Arrays.asList(mockAuthorDTO, mockAuthorDTO, mockAuthorDTO);

        when(authorRepository.findAll(any(Sort.class))).thenReturn(entityList);
        when(authorMapper.entityToDto(entityList)).thenReturn(dtoList);

        List<AuthorDTO> authorDTOS = contentService.getAllAuthors();
        assertThat(authorDTOS).hasSize(3);
    }

    @Test
    void getAuthorById() {
        Author mockAuthorEntity = mock(Author.class);
        AuthorDTO mockAuthorDTO = mock(AuthorDTO.class);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(mockAuthorEntity));
        when(authorMapper.entityToDto(any(Author.class))).thenReturn(mockAuthorDTO);

        AuthorDTO dto = contentService.getAuthorById(1L);
        assertThat(dto).isNotNull();

        dto = contentService.getAuthorById(2L);
        assertThat(dto).isNull();
    }

    @Test
    void getBookById() {
        Book mockBookEntity = mock(Book.class);
        BookDTO mockBookDTO = mock(BookDTO.class);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBookEntity));
        when(bookMapper.entityToDto(any(Book.class))).thenReturn(mockBookDTO);

        BookDTO dto = contentService.getBookById(1L);
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
        AuthorDTO authorDTO = new AuthorDTO(null, "Author name");
        AuthorDTO savedAuthorDTO = new AuthorDTO(10L, "Author name");

        when(authorMapper.dtoToEntity(any(AuthorDTO.class)))
                .thenReturn(mock(Author.class));
        when(authorRepository.save(any(Author.class)))
                .thenReturn(mock(Author.class));
        when(authorMapper.entityToDto(any(Author.class)))
                .thenReturn(savedAuthorDTO);

        when(exceptionFactory.create(any(HttpStatus.class), any(ErrorResponseFactory.InternalErrorCode.class)))
                .thenReturn(mock(ResponseBodyException.class));

        assertDoesNotThrow(() -> contentService.createAuthor(authorDTO));
        AuthorDTO returnedAuthorDto = contentService.createAuthor(authorDTO);
        assertThat(returnedAuthorDto.getId()).isNotNull();

        authorDTO.setName("   ");
        assertThrows(ResponseBodyException.class, () -> contentService.createAuthor(authorDTO));

        authorDTO.setName("");
        assertThrows(ResponseBodyException.class, () -> contentService.createAuthor(authorDTO));
    }

    @Test
    void createBook() {
        AuthorDTO someAuthorDto = new AuthorDTO()
                .setId(20L)
                .setName("Author name");
        BookDTO.BookDTOBuilder builder = BookDTO.builder()
                .id(null)
                .name("book name")
                .imagesFileList(new ArrayList<>())
                .price(new BigDecimal("100.00"))
                .releaseYear(2022)
                .stock(mock(StockDTO.class))
                .mainFile(new BookImageDTO(10L, ImageType.MAIN, new byte[1024]))
                .authors(Arrays.asList(someAuthorDto));
        BookDTO bookDTO = builder.build();
        BookDTO savedBookDTO = builder.id(30L).build();

        when(bookMapper.dtoToEntity(any(BookDTO.class)))
                .thenReturn(mock(Book.class));
        when(bookRepository.save(any(Book.class)))
                .thenReturn(mock(Book.class));
        when(bookMapper.entityToDto(any(Book.class)))
                .thenReturn(savedBookDTO);

        when(exceptionFactory.create(any(HttpStatus.class), any(ErrorResponseFactory.InternalErrorCode.class)))
                .thenReturn(mock(ResponseBodyException.class));

        assertDoesNotThrow(() -> contentService.createBook(bookDTO));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                bookDTO.setName("    ")
        ));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                bookDTO.setName("")
        ));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                bookDTO.setName("book name").setPrice(new BigDecimal("-10.0"))
        ));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                bookDTO.setPrice(new BigDecimal("10.0")).setReleaseYear(-10)
        ));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                bookDTO.setReleaseYear(2022).setAuthors(Arrays.asList(someAuthorDto, null))
        ));
        assertThrows(ResponseBodyException.class, () -> contentService.createBook(
                bookDTO.setReleaseYear(2022).setAuthors(new ArrayList<>())
        ));
    }
}
package com.example.booksserver.service;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.map.AuthorMapper;
import com.example.booksserver.map.BookMapper;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookImageRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ContentServiceTest {
    @MockBean
    private JwtDecoder jwtDecoder;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private BookImageRepository bookImageRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private ContentService contentService;


    @Test
    public void testGetAuthorById() {
        Author author = mock(Author.class);
        when(authorRepository.findAuthorById(any(Long.class))).thenReturn(author);
        AuthorDTO authorDTO = mock(AuthorDTO.class);
        when(authorMapper.entityToDto(any(Author.class))).thenReturn(authorDTO);

        AuthorDTO resultDto;
        resultDto = contentService.getAuthorById(1L);
        assertThat(resultDto).isNotNull();

        resultDto = contentService.getAuthorById(null);
        assertThat(resultDto).isNull();
    }

    @Test
    public void testGetMinMaxPrices() {
        when(bookRepository.findMinPrice()).thenReturn(1000L);
        when(bookRepository.findMaxPrice()).thenReturn(2000L);

        List<Double> minMaxPrices = contentService.getMinMaxPrices();
        assertThat(minMaxPrices).hasSize(2);
        assertThat(minMaxPrices.get(0)).isLessThanOrEqualTo(minMaxPrices.get(1));
    }

    @Test
    public void testGetAuthors() {
//        List<Author> authorList = Arrays.asList(mock(Author.class), mock(Author.class));
//        List<AuthorDTO> authorDTOList = authorList.stream().map(author -> mock(AuthorDTO.class)).toList();
//        Page<AuthorDTO> page = mock(Page.class);

        when(authorRepository.findAll(any(Sort.class))).thenReturn(mock(List.class));
        when(authorRepository.findAll(any(Pageable.class))).thenReturn(mock(Page.class));

        when(authorMapper.dtoToEntity(any(AuthorDTO.class))).thenReturn(mock(Author.class));
        when(authorMapper.dtoToEntity(anyList())).thenReturn(mock(List.class));

        List<AuthorDTO> dtoList;
        dtoList = contentService.getAuthors(new AuthorsFilters(null, null));
        assertThat(dtoList).isNotNull();

        dtoList = contentService.getAuthors(new AuthorsFilters(0, 20));
        assertThat(dtoList).isNotNull();
    }

    @Test
    public void testGetBooks() {
        when(bookRepository.findMinPrice()).thenReturn(1000L);
        when(bookRepository.findMaxPrice()).thenReturn(2000L);

        when(bookRepository.findAllByAuthors_idAndPriceBetween(anyLong(), anyLong(), anyLong(), any(Pageable.class))).thenReturn(mock(Page.class));
        when(bookRepository.findAllByPriceBetween(anyLong(), anyLong(), any(Pageable.class))).thenReturn(mock(Page.class));

        when(bookMapper.entityToDto(any(Book.class))).thenReturn(mock(BookDTO.class));
        when(bookMapper.entityToDto(any(List.class))).thenReturn(mock(List.class));

        List<BookDTO> dtoList;
        dtoList = contentService.getBooks(new BooksFilters(null, null, null, 0, 20));
        assertThat(dtoList).isNotNull();

        dtoList = contentService.getBooks(new BooksFilters(1L, 10.00, 20.00, null, null));
        assertThat(dtoList).isNotNull();
    }

    @Test
    public void testCreateAuthor() {
        AuthorDTO authorDTO = new AuthorDTO(null, "Arthur Morgan");
        Author someEntity = new Author();
        someEntity.setName("Arthur Morgan");

        when(authorMapper.dtoToEntity(any(AuthorDTO.class))).thenReturn(someEntity);

        assertDoesNotThrow(() -> contentService.createAuthor(authorDTO));

        authorDTO.setName("    ");
        assertThrows(ResponseStatusException.class, () -> contentService.createAuthor(authorDTO));
    }

    @Test
    public void testCreateBook() {
        BookDTO bookDTO = new BookDTO();


        Book entityMock = mock(Book.class);
        when(bookMapper.dtoToEntity(any(BookDTO.class))).thenReturn(entityMock);

        assertThrows(ResponseStatusException.class, () -> contentService.createBook(bookDTO));
        bookDTO.setName("Some Book Name");
        assertThrows(ResponseStatusException.class, () -> contentService.createBook(bookDTO));
        bookDTO.setPrice(10.00);
        assertThrows(ResponseStatusException.class, () -> contentService.createBook(bookDTO));
        bookDTO.setReleaseYear(2022);
        assertThrows(ResponseStatusException.class, () -> contentService.createBook(bookDTO));
        bookDTO.setMainFile(new BookImageDTO(null, ImageType.MAIN, new byte[1024]));
        assertThrows(ResponseStatusException.class, () -> contentService.createBook(bookDTO));
        bookDTO.getAuthors().add(new AuthorDTO(101L, "Arthur Morgan"));
        assertDoesNotThrow(() -> contentService.createBook(bookDTO));

        bookDTO.setPrice(-20.0);
        assertThrows(ResponseStatusException.class, () -> contentService.createBook(bookDTO));

        bookDTO.setPrice(10.00);
        bookDTO.setReleaseYear(-20);
        assertThrows(ResponseStatusException.class, () -> contentService.createBook(bookDTO));

        bookDTO.setName("   ");
        assertThrows(ResponseStatusException.class, () -> contentService.createBook(bookDTO));

        bookDTO.setName("Some Book Name");
        bookDTO.getAuthors().clear();
        assertThrows(ResponseStatusException.class, () -> contentService.createBook(bookDTO));

    }
}

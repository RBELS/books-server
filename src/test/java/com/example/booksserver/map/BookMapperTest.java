package com.example.booksserver.map;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.dto.StockDTO;
import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.Stock;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.entity.image.ImageType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookMapperTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private BookMapper bookMapper;

    private final Book someBookEntity;
    private final BookDTO someBookDTO;
    {
        BookImage mainImageMock = mock(BookImage.class);
        when(mainImageMock.getType()).thenReturn(ImageType.MAIN);
        someBookEntity = new Book()
                .setId(20L)
                .setName("Book name")
                .setReleaseYear(2020)
                .setPrice(new BigDecimal("10.00"))
                .setImages(Arrays.asList(mainImageMock, mock(BookImage.class)))
                .setStock(mock(Stock.class))
                .setAuthors(Arrays.asList(mock(Author.class), mock(Author.class)));

        someBookDTO = new BookDTO()
                .setId(20L)
                .setName("Book name")
                .setReleaseYear(2020)
                .setPrice(new BigDecimal("10.00"))
                .setMainFile(mock(BookImageDTO.class))
                .setImagesFileList(Arrays.asList(mock(BookImageDTO.class), mock(BookImageDTO.class), mock(BookImageDTO.class)))
                .setStock(mock(StockDTO.class))
                .setAuthors(Arrays.asList(mock(AuthorDTO.class), mock(AuthorDTO.class)));
    }

    private void compareEntityToDto(Book entity, BookDTO dto) {
        assertThat(dto).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getReleaseYear()).isEqualTo(dto.getReleaseYear());
        assertThat(entity.getPrice()).isEqualTo(dto.getPrice());

        assertThat(dto.getStock()).isNotNull();
        assertThat(dto.getImagesFileList()).isNotNull();
        assertThat(dto.getAuthors()).isNotNull();

        assertThat(dto.getMainFile()).isNotNull();
        assertThat(dto.getImagesFileList()).isNotNull();
    }

    @Test
    void entityToDto() {
        BookDTO dto = bookMapper.entityToDto(someBookEntity);
        compareEntityToDto(someBookEntity, dto);
    }

    private void compareEntityToDtoList(List<Book> entityList, List<BookDTO> dtoList) {
        assertThat(entityList.size()).isEqualTo(dtoList.size());

        for (int i = 0;i < entityList.size();i++) {
            compareEntityToDto(entityList.get(i), dtoList.get(i));
        }
    }

    @Test
    void entityToDtoList() {
        List<Book> entityList = Arrays.asList(someBookEntity, someBookEntity);
        List<BookDTO> dtoList = bookMapper.entityToDto(entityList);

        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void dtoToEntity() {
        Book entity = bookMapper.dtoToEntity(someBookDTO);

        compareEntityToDto(entity, someBookDTO);
    }

    @Test
    void entityToDtoPage() {
        List<Book> entityList = Arrays.asList(
                someBookEntity,
                someBookEntity,
                someBookEntity
        );
        Pageable pageable = PageRequest.of(0, 3);
        long total = 100;
        Page<Book> entityPage = new PageImpl<>(entityList, pageable, total);
        Page<BookDTO> dtoPage = bookMapper.entityToDtoPage(entityPage);

        assertThat(dtoPage.getNumber()).isEqualTo(entityPage.getNumber());
        assertThat(dtoPage.getSize()).isEqualTo(entityPage.getSize());
        assertThat(dtoPage.getTotalElements()).isEqualTo(entityPage.getTotalElements());
        assertThat(dtoPage.getTotalPages()).isEqualTo(entityPage.getTotalPages());

        compareEntityToDtoList(entityPage.getContent(), dtoPage.getContent());
    }
}
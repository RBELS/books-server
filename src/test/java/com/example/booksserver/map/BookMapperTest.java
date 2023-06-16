package com.example.booksserver.map;

import com.example.booksserver.model.service.Author;
import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.service.BookImage;
import com.example.booksserver.model.service.Stock;
import com.example.booksserver.model.entity.AuthorEntity;
import com.example.booksserver.model.entity.BookEntity;
import com.example.booksserver.model.entity.StockEntity;
import com.example.booksserver.model.entity.BookImageEntity;
import com.example.booksserver.model.entity.ImageType;
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

    private final BookEntity someBookEntity;
    private final Book someBook;
    {
        BookImageEntity mainImageMock = mock(BookImageEntity.class);
        when(mainImageMock.getType()).thenReturn(ImageType.MAIN);
        someBookEntity = new BookEntity()
                .setId(20L)
                .setName("Book name")
                .setReleaseYear(2020)
                .setPrice(new BigDecimal("10.00"))
                .setImages(Arrays.asList(mainImageMock, mock(BookImageEntity.class)))
                .setStock(mock(StockEntity.class))
                .setAuthors(Arrays.asList(mock(AuthorEntity.class), mock(AuthorEntity.class)));

        someBook = new Book()
                .setId(20L)
                .setName("Book name")
                .setReleaseYear(2020)
                .setPrice(new BigDecimal("10.00"))
                .setMainFile(mock(BookImage.class))
                .setImagesFileList(Arrays.asList(mock(BookImage.class), mock(BookImage.class), mock(BookImage.class)))
                .setStock(mock(Stock.class))
                .setAuthors(Arrays.asList(mock(Author.class), mock(Author.class)));
    }

    private void compareEntityToService(BookEntity entity, Book serviceObj) {
        assertThat(serviceObj).isNotNull();
        assertThat(entity.getId()).isEqualTo(serviceObj.getId());
        assertThat(entity.getName()).isEqualTo(serviceObj.getName());
        assertThat(entity.getReleaseYear()).isEqualTo(serviceObj.getReleaseYear());
        assertThat(entity.getPrice()).isEqualTo(serviceObj.getPrice());

        assertThat(serviceObj.getStock()).isNotNull();
        assertThat(serviceObj.getImagesFileList()).isNotNull();
        assertThat(serviceObj.getAuthors()).isNotNull();

        assertThat(serviceObj.getMainFile()).isNotNull();
        assertThat(serviceObj.getImagesFileList()).isNotNull();
    }

    @Test
    void entityToService() {
        Book serviceObj = bookMapper.entityToService(someBookEntity);
        compareEntityToService(someBookEntity, serviceObj);
    }

    private void compareEntityToDtoList(List<BookEntity> entityList, List<Book> serviceObjList) {
        assertThat(entityList).hasSameSizeAs(serviceObjList);

        for (int i = 0;i < entityList.size();i++) {
            compareEntityToService(entityList.get(i), serviceObjList.get(i));
        }
    }

    @Test
    void entityToServiceList() {
        List<BookEntity> entityList = Arrays.asList(someBookEntity, someBookEntity);
        List<Book> dtoList = bookMapper.entityToService(entityList);

        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void serviceToEntity() {
        BookEntity entity = bookMapper.serviceToEntity(someBook);

        compareEntityToService(entity, someBook);
    }

    @Test
    void entityToServicePage() {
        List<BookEntity> entityList = Arrays.asList(
                someBookEntity,
                someBookEntity,
                someBookEntity
        );
        Pageable pageable = PageRequest.of(0, 3);
        long total = 100;
        Page<BookEntity> entityPage = new PageImpl<>(entityList, pageable, total);
        Page<Book> dtoPage = bookMapper.entityToServicePage(entityPage);

        assertThat(dtoPage.getNumber()).isEqualTo(entityPage.getNumber());
        assertThat(dtoPage.getSize()).isEqualTo(entityPage.getSize());
        assertThat(dtoPage.getTotalElements()).isEqualTo(entityPage.getTotalElements());
        assertThat(dtoPage.getTotalPages()).isEqualTo(entityPage.getTotalPages());

        compareEntityToDtoList(entityPage.getContent(), dtoPage.getContent());
    }
}
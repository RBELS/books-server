package com.example.booksserver.map;

import com.example.booksserver.model.service.Author;
import com.example.booksserver.model.entity.AuthorEntity;
import com.example.booksserver.model.entity.BookEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthorMapperTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private AuthorMapper authorMapper;

    @Test
    public void entityToService() {
        AuthorEntity entity = new AuthorEntity()
                .setId(20L)
                .setName("Arthur Morgan");
        entity.getBooks().add(mock(BookEntity.class));
        entity.getBooks().add(mock(BookEntity.class));
        entity.getBooks().add(mock(BookEntity.class));

        Author dto = authorMapper.entityToService(entity);

        assertThat(dto.getName()).isEqualTo(entity.getName());
        assertThat(dto.getId()).isEqualTo(entity.getId());
    }

    @Test
    public void serviceToEntity() {
        Author dto = new Author()
                .setId(20L)
                .setName("Arthur Morgan");
        AuthorEntity entity = authorMapper.serviceToEntity(dto);

        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getId()).isEqualTo(dto.getId());

        assertThat(entity.getBooks()).isNotNull();
        assertThat(entity.getBooks()).isEmpty();
    }

    private void compareEntityToServiceList(List<AuthorEntity> entityList, List<Author> serviceObjList) {
        assertThat(serviceObjList).hasSameSizeAs(entityList);

        for (int i = 0;i < entityList.size();i++) {
            AuthorEntity entity = entityList.get(i);
            Author dto = serviceObjList.get(i);

            assertThat(entity.getId()).isEqualTo(dto.getId());
            assertThat(entity.getName()).isEqualTo(dto.getName());
            assertThat(entity.getBooks()).isNotNull();
            assertThat(entity.getBooks()).isEmpty();
        }
    }

    @Test
    public void entityToServiceList() {
        List<AuthorEntity> entityList = Arrays.asList(
                new AuthorEntity().setId(20L).setName("name1"),
                new AuthorEntity().setId(30L).setName("name2"),
                new AuthorEntity().setId(40L).setName("name3")
        );
        List<Author> dtoList = authorMapper.entityToService(entityList);
        compareEntityToServiceList(entityList, dtoList);
    }

    @Test
    public void serviceToEntityList() {
        List<Author> dtoList = Arrays.asList(
                new Author().setId(20L).setName("name1"),
                new Author().setId(30L).setName("name2"),
                new Author().setId(40L).setName("name3")
        );
        List<AuthorEntity> entityList = authorMapper.serviceToEntity(dtoList);
        compareEntityToServiceList(entityList, dtoList);
    }

    private void compareEntityToServicePage(Page<AuthorEntity> entityPage, Page<Author> servicePage) {
        assertThat(servicePage.getNumber()).isEqualTo(entityPage.getNumber());
        assertThat(servicePage.getSize()).isEqualTo(entityPage.getSize());
        assertThat(servicePage.getTotalElements()).isEqualTo(entityPage.getTotalElements());
        assertThat(servicePage.getTotalPages()).isEqualTo(entityPage.getTotalPages());
        compareEntityToServiceList(entityPage.getContent(), servicePage.getContent());
    }

    @Test
    public void entityToServicePage() {
        List<AuthorEntity> entityList = Arrays.asList(
                new AuthorEntity().setId(20L).setName("name1"),
                new AuthorEntity().setId(30L).setName("name2"),
                new AuthorEntity().setId(40L).setName("name3")
        );
        Pageable pageable = PageRequest.of(0, 3);
        long total = 100;
        Page<AuthorEntity> entityPage = new PageImpl<>(entityList, pageable, total);
        Page<Author> dtoPage = authorMapper.entityToServicePage(entityPage);

        compareEntityToServicePage(entityPage, dtoPage);
    }

    @Test
    public void serviceToEntityPage() {
        List<Author> dtoList = Arrays.asList(
                new Author().setId(20L).setName("name1"),
                new Author().setId(30L).setName("name2"),
                new Author().setId(40L).setName("name3")
        );
        Pageable pageable = PageRequest.of(0, 3);
        long total = 100;
        Page<Author> dtoPage = new PageImpl<>(dtoList, pageable, total);
        Page<AuthorEntity> entityPage = authorMapper.serviceToEntityPage(dtoPage);

        compareEntityToServicePage(entityPage, dtoPage);
    }
}

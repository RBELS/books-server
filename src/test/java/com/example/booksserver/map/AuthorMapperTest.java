package com.example.booksserver.map;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.map.AuthorMapper;
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
    public void entityToDto() {
        Author entity = new Author()
                .setId(20L)
                .setName("Arthur Morgan");
        entity.getBooks().add(mock(Book.class));
        entity.getBooks().add(mock(Book.class));
        entity.getBooks().add(mock(Book.class));

        AuthorDTO dto = authorMapper.entityToDto(entity);

        assertThat(dto.getName()).isEqualTo(entity.getName());
        assertThat(dto.getId()).isEqualTo(entity.getId());
    }

    @Test
    public void dtoToEntity() {
        AuthorDTO dto = new AuthorDTO()
                .setId(20L)
                .setName("Arthur Morgan");
        Author entity = authorMapper.dtoToEntity(dto);

        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getId()).isEqualTo(dto.getId());

        assertThat(entity.getBooks()).isNotNull();
        assertThat(entity.getBooks()).isEmpty();
    }

    private void compareEntityToDtoList(List<Author> entityList, List<AuthorDTO> dtoList) {
        assertThat(dtoList.size()).isEqualTo(entityList.size());

        for (int i = 0;i < entityList.size();i++) {
            Author entity = entityList.get(i);
            AuthorDTO dto = dtoList.get(i);

            assertThat(entity.getId()).isEqualTo(dto.getId());
            assertThat(entity.getName()).isEqualTo(dto.getName());
            assertThat(entity.getBooks()).isNotNull();
            assertThat(entity.getBooks()).isEmpty();
        }
    }

    @Test
    public void entityToDtoList() {
        List<Author> entityList = Arrays.asList(
                new Author().setId(20L).setName("name1"),
                new Author().setId(30L).setName("name2"),
                new Author().setId(40L).setName("name3")
        );
        List<AuthorDTO> dtoList = authorMapper.entityToDto(entityList);
        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    public void dtoToEntityList() {
        List<AuthorDTO> dtoList = Arrays.asList(
                new AuthorDTO().setId(20L).setName("name1"),
                new AuthorDTO().setId(30L).setName("name2"),
                new AuthorDTO().setId(40L).setName("name3")
        );
        List<Author> entityList = authorMapper.dtoToEntity(dtoList);
        compareEntityToDtoList(entityList, dtoList);
    }

    private void compareEntityToDtoPage(Page<Author> entityPage, Page<AuthorDTO> dtoPage) {
        assertThat(dtoPage.getNumber()).isEqualTo(entityPage.getNumber());
        assertThat(dtoPage.getSize()).isEqualTo(entityPage.getSize());
        assertThat(dtoPage.getTotalElements()).isEqualTo(entityPage.getTotalElements());
        assertThat(dtoPage.getTotalPages()).isEqualTo(entityPage.getTotalPages());
        compareEntityToDtoList(entityPage.getContent(), dtoPage.getContent());
    }

    @Test
    public void entityToDtoPage() {
        List<Author> entityList = Arrays.asList(
                new Author().setId(20L).setName("name1"),
                new Author().setId(30L).setName("name2"),
                new Author().setId(40L).setName("name3")
        );
        Pageable pageable = PageRequest.of(0, 3);
        long total = 100;
        Page<Author> entityPage = new PageImpl<>(entityList, pageable, total);
        Page<AuthorDTO> dtoPage = authorMapper.entityToDtoPage(entityPage);

        compareEntityToDtoPage(entityPage, dtoPage);
    }

    @Test
    public void dtoToEntityPage() {
        List<AuthorDTO> dtoList = Arrays.asList(
                new AuthorDTO().setId(20L).setName("name1"),
                new AuthorDTO().setId(30L).setName("name2"),
                new AuthorDTO().setId(40L).setName("name3")
        );
        Pageable pageable = PageRequest.of(0, 3);
        long total = 100;
        Page<AuthorDTO> dtoPage = new PageImpl<>(dtoList, pageable, total);
        Page<Author> entityPage = authorMapper.dtoToEntityPage(dtoPage);

        compareEntityToDtoPage(entityPage, dtoPage);
    }
}

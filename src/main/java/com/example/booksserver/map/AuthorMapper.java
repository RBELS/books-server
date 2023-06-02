package com.example.booksserver.map;

import com.example.booksserver.dto.Author;
import com.example.booksserver.entity.AuthorEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class AuthorMapper {
    public abstract Author entityToDto(AuthorEntity entity);
    public abstract List<Author> entityToDto(List<AuthorEntity> entityList);
    public abstract AuthorEntity dtoToEntity(Author dto);
    public abstract List<AuthorEntity> dtoToEntity(List<Author> dtoList);

    public Page<Author> entityToDtoPage(Page<AuthorEntity> books) {
        return books.map(this::entityToDto);
    }

    public Page<AuthorEntity> dtoToEntityPage(Page<Author> bookDTOS) {
        return bookDTOS.map(this::dtoToEntity);
    }
}

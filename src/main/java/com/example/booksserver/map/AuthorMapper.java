package com.example.booksserver.map;

import com.example.booksserver.model.service.Author;
import com.example.booksserver.model.entity.AuthorEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class AuthorMapper {
    public abstract Author entityToService(AuthorEntity entity);
    public abstract List<Author> entityToService(List<AuthorEntity> entityList);
    public abstract AuthorEntity serviceToEntity(Author serviceObj);
    public abstract List<AuthorEntity> serviceToEntity(List<Author> serviceObjList);

    public Page<Author> entityToServicePage(Page<AuthorEntity> books) {
        return books.map(this::entityToService);
    }

    public Page<AuthorEntity> serviceToEntityPage(Page<Author> bookServiceObjects) {
        return bookServiceObjects.map(this::serviceToEntity);
    }
}

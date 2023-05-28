package com.example.booksserver.map;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.entity.Author;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class AuthorMapper {
    public abstract AuthorDTO entityToDto(Author entity);
    public abstract List<AuthorDTO> entityToDto(List<Author> entityList);
    public abstract Author dtoToEntity(AuthorDTO dto);
    public abstract List<Author> dtoToEntity(List<AuthorDTO> dtoList);

    public Page<AuthorDTO> entityToDtoPage(Page<Author> books) {
        return books.map(this::entityToDto);
    }

    public Page<Author> dtoToEntityPage(Page<AuthorDTO> bookDTOS) {
        return bookDTOS.map(this::dtoToEntity);
    }
}

package com.example.booksserver.map;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.entity.Author;
import com.example.booksserver.repository.AuthorRepository;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;


@Mapper(componentModel = "spring")
public abstract class AuthorMapper {
    public abstract AuthorDTO entityToDto(Author entity);
    public abstract List<AuthorDTO> entityToDto(List<Author> entityList);
    public abstract Author dtoToEntity(AuthorDTO dto);
    public abstract List<Author> dtoToEntity(List<AuthorDTO> dtoList);
}

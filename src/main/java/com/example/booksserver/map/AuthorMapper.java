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
    @Autowired
    public AuthorRepository authorRepository;

    public abstract AuthorDTO entityToDto(Author entity);
    public abstract List<AuthorDTO> entityToDto(List<Author> entityList);

    public Author dtoToEntity(AuthorDTO dto) {
        Author entity = authorRepository.findAuthorById(dto.getId());
        if (Objects.isNull(entity)) {
            entity = new Author();
            entity.setId(dto.getId());
            entity.setName(dto.getName());
        }
        return entity;
    }
    public abstract List<Author> dtoToEntity(List<AuthorDTO> dtoList);

}

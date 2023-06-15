package com.example.booksserver.model.dto;

import com.example.booksserver.model.service.Author;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDTO {
    private long id;
    private String name;

    public AuthorDTO(Author author) {
        this.id = author.getId();
        this.name = author.getName();
    }
}

package com.example.booksserver.userstate;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.entity.Author;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAuthor {
    private final long id;
    private final String name;

    public UserAuthor(AuthorDTO author) {
        this.id = author.getId();
        this.name = author.getName();
    }
}

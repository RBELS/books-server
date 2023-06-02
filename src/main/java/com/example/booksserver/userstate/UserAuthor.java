package com.example.booksserver.userstate;

import com.example.booksserver.dto.Author;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthor {
    private long id;
    private String name;

    public UserAuthor(Author author) {
        this.id = author.getId();
        this.name = author.getName();
    }
}

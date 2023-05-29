package com.example.booksserver.userstate;

import com.example.booksserver.dto.AuthorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthor {
    private long id;
    private String name;

    public UserAuthor(AuthorDTO author) {
        this.id = author.getId();
        this.name = author.getName();
    }
}

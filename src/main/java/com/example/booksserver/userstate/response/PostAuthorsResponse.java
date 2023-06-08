package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.Author;
import com.example.booksserver.userstate.UserAuthor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostAuthorsResponse {
    private String status;
    private UserAuthor author;

    public PostAuthorsResponse(Author author) {
        this.status = "SUCCESS";
        this.author = new UserAuthor(author);
    }
}

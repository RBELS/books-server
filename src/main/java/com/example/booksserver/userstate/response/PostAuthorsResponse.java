package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.userstate.UserAuthor;
import lombok.Getter;

@Getter
public class PostAuthorsResponse {
    private final String status;
    private final UserAuthor author;

    public PostAuthorsResponse(AuthorDTO authorDTO) {
        this.status = "SUCCESS";
        this.author = new UserAuthor(authorDTO);
    }
}

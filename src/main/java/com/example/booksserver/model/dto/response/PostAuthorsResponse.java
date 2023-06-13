package com.example.booksserver.model.dto.response;

import com.example.booksserver.model.service.Author;
import com.example.booksserver.model.dto.AuthorDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostAuthorsResponse {
    private String status;
    private AuthorDTO author;

    public PostAuthorsResponse(Author author) {
        this.status = "SUCCESS";
        this.author = new AuthorDTO(author);
    }
}

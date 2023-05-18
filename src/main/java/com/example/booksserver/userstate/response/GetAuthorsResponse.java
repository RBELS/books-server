package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.userstate.UserAuthor;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import lombok.Getter;

import java.util.List;

@Getter
public class GetAuthorsResponse extends PaginatedResponse {
    private final List<UserAuthor> content;

    public GetAuthorsResponse(AuthorsFilters authorsFilters, List<AuthorDTO> content) {
        super(authorsFilters);
        this.content = content.stream().map(UserAuthor::new).toList();
    }
}

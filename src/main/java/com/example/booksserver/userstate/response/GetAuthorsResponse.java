package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.userstate.UserAuthor;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class GetAuthorsResponse extends PaginatedResponse {
    private final List<UserAuthor> content;

    public GetAuthorsResponse(Page<AuthorDTO> content) {
        super(content);
        this.content = content.stream().map(UserAuthor::new).toList();
    }
}

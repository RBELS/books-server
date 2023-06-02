package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.Author;
import com.example.booksserver.userstate.UserAuthor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GetAuthorsResponse extends PaginatedResponse {
    private List<UserAuthor> content;

    public GetAuthorsResponse(Page<Author> content) {
        super(content);
        this.content = content.stream().map(UserAuthor::new).toList();
    }
}

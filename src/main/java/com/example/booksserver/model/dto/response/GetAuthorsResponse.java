package com.example.booksserver.model.dto.response;

import com.example.booksserver.model.service.Author;
import com.example.booksserver.model.dto.AuthorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GetAuthorsResponse extends PaginatedResponse {
    private List<AuthorDTO> content;

    public GetAuthorsResponse(Page<Author> content) {
        super(content);
        this.content = content.stream().map(AuthorDTO::new).toList();
    }
}

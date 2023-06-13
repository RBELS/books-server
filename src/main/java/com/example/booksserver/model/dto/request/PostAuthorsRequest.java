package com.example.booksserver.model.dto.request;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class PostAuthorsRequest {
    private String name;
}

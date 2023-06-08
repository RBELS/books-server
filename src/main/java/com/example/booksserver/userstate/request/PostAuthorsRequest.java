package com.example.booksserver.userstate.request;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class PostAuthorsRequest {
    private String name;
}

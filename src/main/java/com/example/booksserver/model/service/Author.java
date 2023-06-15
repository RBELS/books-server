package com.example.booksserver.model.service;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Author {
    private Long id;
    private String name;
}

package com.example.booksserver.dto;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class AuthorDTO {
    private Long id;
    private String name;
}

package com.example.booksserver.model.service;

import com.example.booksserver.model.entity.ImageType;
import lombok.*;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class BookImage {
    private Long id;
    private ImageType type;
    @ToString.Exclude
    private byte[] content;
}

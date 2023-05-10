package com.example.booksserver.dto;

import com.example.booksserver.entity.image.ImageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class BookImageDTO {
    private Long id;
    private ImageType type;
    private byte[] content;
}

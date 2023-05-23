package com.example.booksserver.dto;

import com.example.booksserver.entity.image.ImageType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookImageDTO {
    private Long id;
    private ImageType type;
    @ToString.Exclude
    private byte[] content;
}

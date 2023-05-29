package com.example.booksserver.dto;

import com.example.booksserver.entity.image.ImageType;
import lombok.*;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class BookImageDTO {
    private Long id;
    private ImageType type;
    @ToString.Exclude
    private byte[] content;
}

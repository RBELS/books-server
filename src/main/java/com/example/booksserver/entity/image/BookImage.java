package com.example.booksserver.entity.image;

import com.example.booksserver.entity.Book;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BookImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private ImageType type;

    @Lob
    private byte[] content;

    @ManyToOne
    @JoinColumn
    private Book book;
}

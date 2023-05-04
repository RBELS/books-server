package com.example.booksserver.entity.image;

import com.example.booksserver.entity.Book;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ImageType type;

    private String source;

    @ManyToOne
    @JoinColumn
    private Book book;
}

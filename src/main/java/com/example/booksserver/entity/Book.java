package com.example.booksserver.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter @Setter @ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private long price;
    private int releaseYear;

    private String imageSrc;

    @ManyToOne
    @ToString.Exclude
    private Author author;
}

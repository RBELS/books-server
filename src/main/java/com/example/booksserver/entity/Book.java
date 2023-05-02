package com.example.booksserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private long price;
    private int releaseYear;

    private String imageSrc;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable
    private Set<Author> authors = new HashSet<>();
}

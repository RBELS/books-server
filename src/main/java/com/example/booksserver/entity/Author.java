package com.example.booksserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter @Setter
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    public Author(String name) {
        this.name = name;
        this.books = new HashSet<>();
    }
}

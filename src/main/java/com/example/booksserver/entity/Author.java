package com.example.booksserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    public Author(String name) {
        this.name = name;
        this.books = new HashSet<>();
    }
}

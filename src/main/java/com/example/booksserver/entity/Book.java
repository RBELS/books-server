package com.example.booksserver.entity;

import com.example.booksserver.entity.image.BookImage;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private int releaseYear;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable
    private List<Author> authors = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
    private List<BookImage> images = new ArrayList<>();
}

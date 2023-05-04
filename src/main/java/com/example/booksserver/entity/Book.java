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
@AllArgsConstructor
@Getter @Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private long price;
    private int releaseYear;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable
    private Set<Author> authors = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
    private List<BookImage> images = new ArrayList<>();
}

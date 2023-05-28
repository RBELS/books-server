package com.example.booksserver.entity;

import com.example.booksserver.entity.image.BookImage;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer releaseYear;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable
    private List<Author> authors = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
    private List<BookImage> images = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    private Stock stock;
}

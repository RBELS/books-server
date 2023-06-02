package com.example.booksserver.entity;

import com.example.booksserver.entity.image.BookImageEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Accessors(chain = true)
@Table(name = "book")
public class BookEntity {
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
    private List<AuthorEntity> authors = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
    private List<BookImageEntity> images = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    private StockEntity stock;
}

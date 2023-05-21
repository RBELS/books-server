package com.example.booksserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.MERGE)
    private Book book;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Long price;
}

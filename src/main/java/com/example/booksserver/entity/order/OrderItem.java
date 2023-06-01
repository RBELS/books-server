package com.example.booksserver.entity.order;

import com.example.booksserver.entity.Book;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@Accessors(chain = true)
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
    private BigDecimal price;
}

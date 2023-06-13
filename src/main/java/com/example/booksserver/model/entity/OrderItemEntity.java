package com.example.booksserver.model.entity;

import com.example.booksserver.model.entity.BookEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "order_item")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.MERGE)
    private BookEntity book;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private BigDecimal price;
}

package com.example.booksserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@NoArgsConstructor
@Table(name = "stock")
@Accessors(chain = true)
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Integer available;

    @Column(nullable = false)
    private Integer inDelivery;

    @Column(nullable = false)
    private Integer ordered;
}

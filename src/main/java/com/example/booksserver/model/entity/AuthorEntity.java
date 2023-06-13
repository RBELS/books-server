package com.example.booksserver.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Accessors(chain = true)
@Table(name = "author")
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "authors")
    private Set<BookEntity> books = new HashSet<>();

    public AuthorEntity(String name) {
        this.name = name;
        this.books = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorEntity authorEntity = (AuthorEntity) o;
        return Objects.equals(id, authorEntity.id) && Objects.equals(name, authorEntity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

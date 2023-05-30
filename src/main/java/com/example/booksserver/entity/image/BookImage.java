package com.example.booksserver.entity.image;

import com.example.booksserver.entity.Book;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BookImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private ImageType type;

    @Lob
    private byte[] content;

    @ManyToOne
    @JoinColumn
    private Book book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookImage bookImage = (BookImage) o;
        return Objects.equals(id, bookImage.id) && type == bookImage.type && Arrays.equals(content, bookImage.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, type);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}

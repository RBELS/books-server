package com.example.booksserver.entity.image;

import com.example.booksserver.entity.BookEntity;
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
public class BookImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private ImageType type;

    @Lob
    private byte[] content;

    @ManyToOne
    @JoinColumn
    private BookEntity book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookImageEntity bookImageEntity = (BookImageEntity) o;
        return Objects.equals(id, bookImageEntity.id) && type == bookImageEntity.type && Arrays.equals(content, bookImageEntity.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, type);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}

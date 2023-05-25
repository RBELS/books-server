package com.example.booksserver.repository;

import com.example.booksserver.entity.image.BookImage;
import lombok.NonNull;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookImageRepository extends ListCrudRepository<BookImage, Long> {
}

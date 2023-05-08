package com.example.booksserver.repository;

import com.example.booksserver.entity.Author;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends PagingAndSortingRepository<Author, Long>, CrudRepository<Author, Long> {
    @NonNull
    List<Author> findAll(@NonNull Sort sort);
    @NonNull
    Page<Author> findAll(@NonNull Pageable pageable);

    Author findAuthorById(@NonNull Long id);
}

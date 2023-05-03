package com.example.booksserver.repository;

import com.example.booksserver.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AuthorRepository extends PagingAndSortingRepository<Author, Long>, CrudRepository<Author, Long> {
    List<Author> findAll(Sort sort);
    Page<Author> findAll(Pageable pageable);
}

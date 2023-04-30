package com.example.booksserver.repository;

import com.example.booksserver.entity.Author;
import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<Author, Long> {

}

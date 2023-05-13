package com.example.booksserver.repo;

import com.example.booksserver.entity.Author;
import com.example.booksserver.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void insertData() {
        authorRepository.saveAll(List.of(
                new Author(null, "Arthur Morgan", new HashSet<>()),
                new Author(null, "Harry Potter", new HashSet<>()),
                new Author(null, "Frodo Baggins", new HashSet<>())
        ));
    }

    @Test
    public void findAllCheckCount() {
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(3);
    }

    @Test
    public void findAuthorById() {
        Author author = authorRepository.findAuthorById(1L);
        assertThat(author).isNotNull();
        assertThat(author.getId()).isEqualTo(1L);
        assertThat(author.getName()).isEqualTo("Arthur Morgan");
        assertThat(author.getBooks()).hasSize(0);
    }
}

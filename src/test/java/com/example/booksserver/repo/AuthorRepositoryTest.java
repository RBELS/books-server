package com.example.booksserver.repo;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class AuthorRepositoryTest {

    public static final String[] FIRST_NAMES = {"Sophia", "Jackson", "Olivia", "Liam", "Emma", "Noah", "Ava", "Ethan", "Isabella", "Lucas", "Mia", "Mason", "Charlotte", "Oliver", "Amelia", "Elijah", "Harper", "Aiden", "Evelyn", "Carter"};
    public static final String[] LAST_NAMES = {"Smith", "Johnson", "Brown", "Garcia", "Miller", "Davis", "Gonzalez", "Wilson", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Moore", "Young", "Allen", "King", "Wright"};
    public static final int AUTHOR_GEN_COUNT = 100;

    @Autowired
    private AuthorRepository authorRepository;

    public static String genRandomAuthorName() {
        return FIRST_NAMES[(int) (Math.random() * FIRST_NAMES.length)] +
                " " +
                LAST_NAMES[(int) (Math.random() * FIRST_NAMES.length)];
    }

    public static List<Author> insertAuthors(AuthorRepository authorRepository, int count) {
        ArrayList<Author> authors = new ArrayList<>();
        for (int i = 0;i < count;i++) {
            Author authorToSave = new Author();
            authorToSave.setName(genRandomAuthorName());
            authors.add(authorToSave);
        }
        return authorRepository.saveAll(authors);
    }

    @BeforeEach
    public void insertAuthorsTest() {
        insertAuthors(authorRepository, AUTHOR_GEN_COUNT);
    }


    @Test
    public void findAllCheckCount() {
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(AUTHOR_GEN_COUNT);
    }

    @Test
    public void findAuthorById() {
        // TODO: replace
        Author author = authorRepository.findById(1L).get();
        assertThat(author).isNotNull();
        assertThat(author.getId()).isEqualTo(1L);
        assertThat(author.getName()).isNotBlank();
        assertThat(author.getBooks()).hasSize(0);
    }

    @Test
    public void test() {
        Author author = new Author();
        author.setId(1L);
        author.setName("name");

        Book book = new Book();
        book.setId(1L);
        book.setAuthors(Stream.of(author).toList());

        author.setBooks(Stream.of(book).collect(Collectors.toSet()));

        System.err.println(author.hashCode());
        System.err.println(author.toString());

        System.err.println(book.hashCode());
        System.err.println(book.toString());

    }
}

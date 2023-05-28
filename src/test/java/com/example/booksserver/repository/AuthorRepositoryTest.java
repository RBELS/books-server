package com.example.booksserver.repository;

import com.example.booksserver.entity.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class AuthorRepositoryTest {
    public static final String[] FIRST_NAMES = {"Sophia", "Jackson", "Olivia", "Liam", "Emma", "Noah", "Ava", "Ethan", "Isabella", "Lucas", "Mia", "Mason", "Charlotte", "Oliver", "Amelia", "Elijah", "Harper", "Aiden", "Evelyn", "Carter"};
    public static final String[] LAST_NAMES = {"Smith", "Johnson", "Brown", "Garcia", "Miller", "Davis", "Gonzalez", "Wilson", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Moore", "Young", "Allen", "King", "Wright"};
    public static final int AUTHOR_GEN_COUNT = 100;

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

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void insertData() {
        insertAuthors(authorRepository, AUTHOR_GEN_COUNT);
    }

    @Test
    public void testFindAllSortedAscending() {
        Sort sort = Sort.by("name", "id").ascending();
        List<Author> authorList = authorRepository.findAll(sort);

        assertThat(authorList.size()).isEqualTo(AUTHOR_GEN_COUNT);

        List<Author> sortedList = authorList.stream().sorted(
                Comparator
                        .comparing(Author::getName)
                        .thenComparingLong(Author::getId)
        ).toList();

        assertThat(authorList).isEqualTo(sortedList);
    }

    @Test
    public void testFindPageSortedDescending() {
        Sort sort = Sort.by("name", "id").descending();

        int page = 0;
        int pageSize = 20;
        Page<Author> authorPage = authorRepository.findAll(
                PageRequest.of(page, pageSize, sort)
        );

        assertThat(authorPage.getTotalPages())
                .isEqualTo((int) Math.ceil(AUTHOR_GEN_COUNT / (double) pageSize));

        assertThat(authorPage.getNumber()).isEqualTo(page);
        assertThat(authorPage.getSize()).isEqualTo(pageSize);

        List<Author> authorList = authorPage.getContent();
        List<Author> sortedList = authorPage.stream().sorted(
                Comparator
                        .comparing(Author::getName)
                        .thenComparing(Author::getId)
                        .reversed()
        ).toList();

        assertThat(authorList).isEqualTo(sortedList);
    }

}

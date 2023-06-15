package com.example.booksserver.repository;

import com.example.booksserver.model.entity.AuthorEntity;
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

    public static List<AuthorEntity> insertAuthors(AuthorRepository authorRepository, int count) {
        ArrayList<AuthorEntity> authorEntities = new ArrayList<>();
        for (int i = 0;i < count;i++) {
            AuthorEntity authorEntityToSave = new AuthorEntity();
            authorEntityToSave.setName(genRandomAuthorName());
            authorEntities.add(authorEntityToSave);
        }
        return authorRepository.saveAll(authorEntities);
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
        List<AuthorEntity> authorEntityList = authorRepository.findAll(sort);

        assertThat(authorEntityList.size()).isEqualTo(AUTHOR_GEN_COUNT);

        List<AuthorEntity> sortedList = authorEntityList.stream().sorted(
                Comparator
                        .comparing(AuthorEntity::getName)
                        .thenComparingLong(AuthorEntity::getId)
        ).toList();

        assertThat(authorEntityList).isEqualTo(sortedList);
    }

    @Test
    public void testFindPageSortedDescending() {
        Sort sort = Sort.by("name", "id").descending();

        int page = 0;
        int pageSize = 20;
        Page<AuthorEntity> authorPage = authorRepository.findAll(
                PageRequest.of(page, pageSize, sort)
        );

        assertThat(authorPage.getTotalPages())
                .isEqualTo((int) Math.ceil(AUTHOR_GEN_COUNT / (double) pageSize));

        assertThat(authorPage.getNumber()).isEqualTo(page);
        assertThat(authorPage.getSize()).isEqualTo(pageSize);

        List<AuthorEntity> authorEntityList = authorPage.getContent();
        List<AuthorEntity> sortedList = authorPage.stream().sorted(
                Comparator
                        .comparing(AuthorEntity::getName)
                        .thenComparing(AuthorEntity::getId)
                        .reversed()
        ).toList();

        assertThat(authorEntityList).isEqualTo(sortedList);
    }

}

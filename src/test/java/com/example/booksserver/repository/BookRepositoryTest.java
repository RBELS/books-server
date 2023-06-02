package com.example.booksserver.repository;

import com.example.booksserver.entity.AuthorEntity;
import com.example.booksserver.entity.BookEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class BookRepositoryTest {
    public final String[] BOOK_NAMES = {"To Kill a Mockingbird", "1984", "The Great Gatsby", "Pride and Prejudice", "Animal Farm", "Brave New World", "The Catcher in the Rye", "Lord of the Flies", "The Hobbit", "The Lord of the Rings", "The Hitchhiker's Guide to the Galaxy", "The Da Vinci Code", "Harry Potter and the Philosopher's Stone", "The Hunger Games", "The Girl with the Dragon Tattoo", "Gone Girl", "The Girl on the Train", "The Fault in Our Stars", "The Alchemist", "The Kite Runner"};
    public static final int BOOKS_GEN_COUNT = 30;

    private AuthorEntity getRandomAuthor() {
        return authorEntityList.get((int) (Math.random() * authorEntityList.size()));
    }

    private String getRandomBookName() {
        return BOOK_NAMES[(int) (Math.random() * BOOK_NAMES.length)];
    }

    private List<BookEntity> insertBooks(BookRepository bookRepository, int count) {
        this.booksList = new ArrayList<>();
        for (int i = 0;i < count;i++) {
            BookEntity bookEntityToSave = new BookEntity();
            bookEntityToSave.setName(getRandomBookName());
            bookEntityToSave.setPrice(new BigDecimal(String.format("%.2f", Math.random() * 100.0)));
            bookEntityToSave.setReleaseYear(2020);
            bookEntityToSave.setAuthors(List.of(getRandomAuthor(), getRandomAuthor()));

            booksList.add(bookEntityToSave);
        }
        return bookRepository.saveAll(booksList);
    }


    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    private List<AuthorEntity> authorEntityList;
    private List<BookEntity> booksList;

    @BeforeEach
    public void fillData() {
        this.authorEntityList = AuthorRepositoryTest.insertAuthors(authorRepository, AuthorRepositoryTest.AUTHOR_GEN_COUNT);
        this.booksList = insertBooks(bookRepository, BOOKS_GEN_COUNT);
    }

    @Test
    public void testMinMaxPrice() {
        List<BookEntity> bookEntityList = bookRepository.findAll();
        BigDecimal
                minPrice = bookEntityList.stream().min(Comparator.comparing(BookEntity::getPrice)).get().getPrice(),
                maxPrice = bookEntityList.stream().max(Comparator.comparing(BookEntity::getPrice)).get().getPrice();

        Optional<BigDecimal>
                selectedMinPrice = bookRepository.findMinPrice(),
                selectedMaxPrice = bookRepository.findMaxPrice();
        assertThat(selectedMinPrice).isPresent();
        assertThat(selectedMaxPrice).isPresent();

        assertThat(selectedMinPrice.get().compareTo(minPrice)).isEqualTo(0);
        assertThat(selectedMaxPrice.get().compareTo(maxPrice)).isEqualTo(0);
    }

    @Test
    public void testFindAllByPriceBetween() {
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal minPrice = new BigDecimal("30.0");
        BigDecimal maxPrice = new BigDecimal("80.0");

        List<BookEntity> bookEntityList = bookRepository.findAll();
        List<BookEntity> expectedList = bookEntityList.stream().filter(book ->
                book.getPrice().compareTo(minPrice) >= 0 && book.getPrice().compareTo(maxPrice) <= 0
        ).toList();

        Page<BookEntity> selectedPage;
        List<BookEntity> selectedList = new ArrayList<>();
        do {
            selectedPage = bookRepository.findAllByPriceBetween(minPrice, maxPrice, pageable);
            selectedList.addAll(selectedPage.getContent());
            pageable = pageable.next();
        } while (selectedPage.getContent().size() != 0);

        assertThat(expectedList).isEqualTo(selectedList);
    }

    @Test
    public void testFindDistinctByAuthors_idInAndPriceBetween() {
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal minPrice = new BigDecimal("30.0");
        BigDecimal maxPrice = new BigDecimal("80.0");
        List<Long> authorIdList = new ArrayList<>();
        for (int i = 0;i < 20;i++) {
            authorIdList.add(getRandomAuthor().getId());
        }


        List<BookEntity> bookEntityList = bookRepository.findAll();
        List<BookEntity> expectedList = bookEntityList.stream().filter(book ->
                book.getPrice().compareTo(minPrice) >= 0 && book.getPrice().compareTo(maxPrice) <= 0
                && book.getAuthors().stream().anyMatch(author -> authorIdList.contains(author.getId()))
        ).toList();

        Page<BookEntity> selectedPage;
        List<BookEntity> selectedList = new ArrayList<>();
        do {
            selectedPage = bookRepository.findDistinctAllByAuthors_idInAndPriceBetween(authorIdList, minPrice, maxPrice, pageable);
            selectedList.addAll(selectedPage.getContent());
            pageable = pageable.next();
        } while (selectedPage.getContent().size() != 0);

        assertThat(expectedList).isEqualTo(selectedList);
    }

    @Test
    public void testFindDistinctByAuthors_idIn() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Long> authorIdList = new ArrayList<>();
        for (int i = 0;i < 30;i++) {
            authorIdList.add(getRandomAuthor().getId());
        }

        List<BookEntity> bookEntityList = bookRepository.findAll();
        List<BookEntity> expectedList = bookEntityList.stream().filter(book ->
                book.getAuthors().stream().anyMatch(author -> authorIdList.contains(author.getId()))
        ).toList();

        Page<BookEntity> selectedPage;
        List<BookEntity> selectedList = new ArrayList<>();
        do {
            selectedPage = bookRepository.findDistinctByAuthors_idIn(authorIdList, pageable);
            selectedList.addAll(selectedPage.getContent());
            pageable = pageable.next();
        } while (selectedPage.getContent().size() != 0);

        System.out.println(expectedList);
        System.out.println(selectedList);
        assertThat(expectedList).isEqualTo(selectedList);
    }
}

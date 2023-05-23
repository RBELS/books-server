package com.example.booksserver.repo;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    public final String[] BOOK_NAMES = {"To Kill a Mockingbird", "1984", "The Great Gatsby", "Pride and Prejudice", "Animal Farm", "Brave New World", "The Catcher in the Rye", "Lord of the Flies", "The Hobbit", "The Lord of the Rings", "The Hitchhiker's Guide to the Galaxy", "The Da Vinci Code", "Harry Potter and the Philosopher's Stone", "The Hunger Games", "The Girl with the Dragon Tattoo", "Gone Girl", "The Girl on the Train", "The Fault in Our Stars", "The Alchemist", "The Kite Runner"};
    public static final int BOOKS_GEN_COUNT = 30;

    private List<Author> authorList;
    private List<Book> booksList;

    @BeforeEach
    public void predefineAuthors() {
        this.authorList = AuthorRepositoryTest.insertAuthors(authorRepository, AuthorRepositoryTest.AUTHOR_GEN_COUNT);
        this.booksList = saveRandomBooks(bookRepository, BOOKS_GEN_COUNT);
    }

    private Author getRandomAuthor() {
        return authorList.get((int) (Math.random() * authorList.size()));
    }

    private String getRandomBookName() {
        return BOOK_NAMES[(int) (Math.random() * BOOK_NAMES.length)];
    }

    private List<Book> saveRandomBooks(BookRepository bookRepository, int count) {
        this.booksList = new ArrayList<>();
        for (int i = 0;i < count;i++) {
            Book bookToSave = new Book();
            bookToSave.setName(getRandomBookName());
            bookToSave.setPrice(800 + (int) (Math.random() * 400));
            bookToSave.setReleaseYear(2020);
            bookToSave.setAuthors(List.of(getRandomAuthor(), getRandomAuthor()));

            booksList.add(bookToSave);
        }
        return bookRepository.saveAll(booksList);
    }

    @Test
    public void testSaveAllBooksCount() {
        assertThat(bookRepository.count()).isEqualTo(BOOKS_GEN_COUNT);
    }

    @Test
    public void testSavedBook() {
        Book bookToSave = new Book();
        bookToSave.setName(getRandomBookName());
        bookToSave.setPrice(1099);
        bookToSave.setReleaseYear(2020);
        bookToSave.getAuthors().add(getRandomAuthor());
        bookToSave.getAuthors().add(getRandomAuthor());

        for (Author author : bookToSave.getAuthors()) {
            author.getBooks().add(bookToSave);
        }

        Book savedEntity = bookRepository.save(bookToSave);

        Optional<Book> optionalBook = bookRepository.findById(savedEntity.getId());
        assertThat(optionalBook).isPresent();
        savedEntity = optionalBook.get();

        assertThat(bookToSave.getName()).isEqualTo(savedEntity.getName());
        assertThat(bookToSave.getPrice()).isEqualTo(savedEntity.getPrice());
        assertThat(bookToSave.getReleaseYear()).isEqualTo(savedEntity.getReleaseYear());
        assertThat(bookToSave.getAuthors()).hasSize(2);

        Book finalSavedEntity = savedEntity;
        savedEntity.getAuthors().forEach(author -> {
            assertThat(author.getBooks()).contains(finalSavedEntity);
        });
    }

    @Test
    public void testPriceFilters() {
        long minPrice = 900, maxPrice = 1000;

        List<Book> bookList = bookRepository.findAllByPriceBetween(minPrice, maxPrice, Pageable.unpaged()).getContent();
        assertThat(bookList.size()).isLessThanOrEqualTo(BOOKS_GEN_COUNT);
        bookList.forEach(book -> {
            assertThat(book.getPrice()).isBetween(minPrice, maxPrice);
        });

    }
}

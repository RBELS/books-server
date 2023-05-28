package com.example.booksserver.repository;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.entity.image.ImageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class BookImageRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookImageRepository bookImageRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void insertData() {
        Book book = new Book();

        book.setName("The Lord of the Rings");
        book.setReleaseYear(2022);
        book.setPrice(new BigDecimal("10.99"));

        Author author = new Author();
        author.setName("Artyom Belsky");
        authorRepository.save(author);

        book.getAuthors().add(author);

        BookImage mainImage = new BookImage();
        byte[] someContent = new byte[1024];
        mainImage.setContent(someContent);
        mainImage.setType(ImageType.MAIN);
        mainImage.setBook(book);

        book.getImages().add(mainImage);

        for (int i = 0;i < 3;i++) {
            BookImage contentImage = new BookImage();
            contentImage.setContent(someContent);
            contentImage.setType(ImageType.CONTENT);
            contentImage.setBook(book);
            book.getImages().add(contentImage);
        }
        bookRepository.save(book);
    }

    @Test
    public void testImages() {
        long count = bookImageRepository.count();
        assertThat(count).isEqualTo(4);
    }

    @Test
    public void testImageContents() {
        List<BookImage> imageList = bookImageRepository.findAll();
        AtomicInteger mainCount = new AtomicInteger(), contentCount = new AtomicInteger();

        imageList.forEach(bookImage -> {
            if (bookImage.getType() == ImageType.MAIN) {
                mainCount.incrementAndGet();
            } else if (bookImage.getType() == ImageType.CONTENT) {
                contentCount.incrementAndGet();
            }
            assertThat(bookImage.getBook()).isNotNull();
            assertThat(bookImage.getContent()).isNotEmpty();
        });

        assertThat(mainCount.get()).isEqualTo(1);
        assertThat(contentCount.get()).isEqualTo(3);
    }
}

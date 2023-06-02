package com.example.booksserver.repository;

import com.example.booksserver.entity.AuthorEntity;
import com.example.booksserver.entity.BookEntity;
import com.example.booksserver.entity.image.BookImageEntity;
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
        BookEntity bookEntity = new BookEntity();

        bookEntity.setName("The Lord of the Rings");
        bookEntity.setReleaseYear(2022);
        bookEntity.setPrice(new BigDecimal("10.99"));

        AuthorEntity authorEntity = new AuthorEntity();
        authorEntity.setName("Artyom Belsky");
        authorRepository.save(authorEntity);

        bookEntity.getAuthors().add(authorEntity);

        BookImageEntity mainImage = new BookImageEntity();
        byte[] someContent = new byte[1024];
        mainImage.setContent(someContent);
        mainImage.setType(ImageType.MAIN);
        mainImage.setBook(bookEntity);

        bookEntity.getImages().add(mainImage);

        for (int i = 0;i < 3;i++) {
            BookImageEntity contentImage = new BookImageEntity();
            contentImage.setContent(someContent);
            contentImage.setType(ImageType.CONTENT);
            contentImage.setBook(bookEntity);
            bookEntity.getImages().add(contentImage);
        }
        bookRepository.save(bookEntity);
    }

    @Test
    public void testImages() {
        long count = bookImageRepository.count();
        assertThat(count).isEqualTo(4);
    }

    @Test
    public void testImageContents() {
        List<BookImageEntity> imageList = bookImageRepository.findAll();
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

package com.example.booksserver.repo;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.repository.BookImageRepository;
import com.example.booksserver.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class BookImageRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookImageRepository bookImageRepository;

    @BeforeEach
    public void init() {
        Book book = new Book();

        book.setName("The Lord of the Rings");
        book.setReleaseYear(2022);
        book.setPrice(1099);

        Author author = new Author();
        author.setName("Artyom Belsky");
        author.getBooks().add(book);

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
}

package com.example.booksserver.rest;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.service.BookService;
import com.example.booksserver.userstate.UserBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RestController
public class TestController {

    Logger logger = LoggerFactory.getLogger("TestController");

    @Autowired
    private BookService bookService;

    @GetMapping("/insert")
    public void insert() {
        logger.info("Insert request");

        Book newBook1 = new Book();
        newBook1.setName("Финансист");
        newBook1.setReleaseYear(2019);
        newBook1.setPrice(1177);
        newBook1.setImageSrc("https://s3-goods.ozstatic.by/480/225/831/10/10831225_0_Finansist_Teodor_Drayzer.jpg");

        Book newBook2 = new Book();
        newBook2.setName("Финансист");
        newBook2.setReleaseYear(2020);
        newBook2.setPrice(1177);
        newBook2.setImageSrc("https://s3-goods.ozstatic.by/480/225/831/10/10831225_0_Finansist_Teodor_Drayzer.jpg");

//        Author author = new Author();
//        author.setName("John Cool");

//        author = authorRepository.save(author);
//
//        newBook1.setAuthor(author);
//        newBook2.setAuthor(author);

//        repository.save(newBook1);
//        repository.save(newBook2);
//        Author savedAuthor = authorRepository.save(author);
//        System.out.println(savedAuthor);

    }

    @GetMapping("/books")
    public Iterable<UserBook> getBooks() {
        Iterable<Book> books = bookService.getAllBooks();
        List<UserBook> userBooksList = new ArrayList<>();
        books.forEach(book -> {
            userBooksList.add(new UserBook(book));
        });
        return userBooksList;
    }


}

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

@RestController
public class TestController {

    Logger logger = LoggerFactory.getLogger("TestController");

    @Autowired
    private BookService bookService;

    private final String[] firstNames = {"Sophia", "Jackson", "Olivia", "Liam", "Emma", "Noah", "Ava", "Ethan", "Isabella", "Lucas", "Mia", "Mason", "Charlotte", "Oliver", "Amelia", "Elijah", "Harper", "Aiden", "Evelyn", "Carter"};
    private final String[] lastNames = {"Smith", "Johnson", "Brown", "Garcia", "Miller", "Davis", "Gonzalez", "Wilson", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Moore", "Young", "Allen", "King", "Wright"};

    private void insertAuthors(int count) {
        for (int i = 0;i < count;i++) {
            String randomFstName = firstNames[(int) (Math.random() * firstNames.length)];
            String randomLastName = lastNames[(int) (Math.random() * lastNames.length)];
            bookService.saveAuthor(new Author(randomFstName + " " + randomLastName));
        }
    }

    String[] bookNames = {"To Kill a Mockingbird", "1984", "The Great Gatsby", "Pride and Prejudice", "Animal Farm", "Brave New World", "The Catcher in the Rye", "Lord of the Flies", "The Hobbit", "The Lord of the Rings", "The Hitchhiker's Guide to the Galaxy", "The Da Vinci Code", "Harry Potter and the Philosopher's Stone", "The Hunger Games", "The Girl with the Dragon Tattoo", "Gone Girl", "The Girl on the Train", "The Fault in Our Stars", "The Alchemist", "The Kite Runner"};

    private void insertBooks(int count) {
        List<Author> authorList = bookService.getAllAuthors();
        for (int i = 0;i < count;i++) {
            Book newBook = new Book();
            newBook.setAuthor(authorList.get((int) (Math.random()*authorList.size())));
            newBook.setName(bookNames[(int) (Math.random()*bookNames.length)]);
            newBook.setPrice((long) (Math.random() * 100000));
            newBook.setReleaseYear(((int)(Math.random() * 50)) + 1970);
            newBook.setImageSrc("https://s3-goods.ozstatic.by/480/225/831/10/10831225_0_Finansist_Teodor_Drayzer.jpg");
            bookService.saveBook(newBook);
        }
    }

    @GetMapping("/insert")
    public void insert() {
        logger.info("Insert request");
        insertAuthors(100);
        insertBooks(100);
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

package com.example.booksserver.fillingtest;

import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.service.ContentService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public final class Filling {

    private final ContentService contentService;
    private final AuthorRepository authorRepository;

    public Filling(ContentService contentService, AuthorRepository authorRepository) {
        this.contentService = contentService;
        this.authorRepository = authorRepository;
    }

    private final String[] firstNames = {"Sophia", "Jackson", "Olivia", "Liam", "Emma", "Noah", "Ava", "Ethan", "Isabella", "Lucas", "Mia", "Mason", "Charlotte", "Oliver", "Amelia", "Elijah", "Harper", "Aiden", "Evelyn", "Carter"};
    private final String[] lastNames = {"Smith", "Johnson", "Brown", "Garcia", "Miller", "Davis", "Gonzalez", "Wilson", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Moore", "Young", "Allen", "King", "Wright"};

    private void insertAuthors(int count) {
        for (int i = 0;i < count;i++) {
            String randomFstName = firstNames[(int) (Math.random() * firstNames.length)];
            String randomLastName = lastNames[(int) (Math.random() * lastNames.length)];
            contentService.saveAuthor(new Author(randomFstName + " " + randomLastName));
        }
    }

    String[] bookNames = {"To Kill a Mockingbird", "1984", "The Great Gatsby", "Pride and Prejudice", "Animal Farm", "Brave New World", "The Catcher in the Rye", "Lord of the Flies", "The Hobbit", "The Lord of the Rings", "The Hitchhiker's Guide to the Galaxy", "The Da Vinci Code", "Harry Potter and the Philosopher's Stone", "The Hunger Games", "The Girl with the Dragon Tattoo", "Gone Girl", "The Girl on the Train", "The Fault in Our Stars", "The Alchemist", "The Kite Runner"};

    private void insertBooks(int count) {
        List<Author> authorList = authorRepository.findAll(Sort.by("name", "id").ascending());
        for (int i = 0;i < count;i++) {
            Book newBook = new Book();
            int authorCount = ((int) (Math.random() * 3)) + 1;
            for (int k = 0;k < authorCount;k++) {
                Author someAuthor = authorList.get((int) (Math.random() * authorList.size()));
                newBook.getAuthors().add(someAuthor);
            }
            newBook.setName(bookNames[(int) (Math.random()*bookNames.length)]);
            newBook.setPrice((long) (Math.random() * 100000));
            newBook.setReleaseYear(((int)(Math.random() * 50)) + 1970);
            String onlySrc = "https://s3-goods.ozstatic.by/480/225/831/10/10831225_0_Finansist_Teodor_Drayzer.jpg";

            BookImage mainImage = new BookImage(null, ImageType.MAIN, onlySrc, newBook);
            newBook.getImages().add(mainImage);

            //generate content images
            for (int k = 0;k < 3;k++) {
                BookImage newImage = new BookImage(null, ImageType.CONTENT, onlySrc, newBook);
                newBook.getImages().add(newImage);
            }

            contentService.saveBook(newBook);
        }
    }

    public void insertInitialData(int authorsCount, int booksCount) {
        insertAuthors(authorsCount);
        insertBooks(booksCount);
    }
}

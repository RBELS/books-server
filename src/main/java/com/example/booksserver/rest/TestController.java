package com.example.booksserver.rest;

import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.Order;
import com.example.booksserver.entity.OrderItem;
import com.example.booksserver.fillingtest.Filling;
import com.example.booksserver.map.BookMapper;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookImageRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final Filling dbFillComponent;
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final BookImageRepository bookImageRepository;
    private final OrderRepository orderRepository;

    private final BookMapper bookMapper;

    public TestController(Filling dbFillComponent, AuthorRepository authorRepository, BookRepository bookRepository, BookImageRepository bookImageRepository, OrderRepository orderRepository, BookMapper bookMapper) {
        this.dbFillComponent = dbFillComponent;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.bookImageRepository = bookImageRepository;
        this.orderRepository = orderRepository;
        this.bookMapper = bookMapper;
    }

    @GetMapping("/test")//3652
    @Transactional
    public void test() {
//        Book bookEntity = bookRepository.findById(3652L).get();
//        BookDTO dto = bookMapper.entityToDto(bookEntity);
//        Book newBook = bookMapper.dtoToEntity(dto);
//        Author newAuthor = new Author();
//        newAuthor.setId(5852L);
//        newAuthor.setName("Joe Biden");
//        newBook.getAuthors().add(newAuthor);
//        newBook.getAuthors().clear();
//        bookRepository.save(newBook);
//        logger.info(dto.toString());
    }


    @GetMapping("/insert")
    public void insert() {
        logger.info("[TEST] Insert request");
        dbFillComponent.insertInitialData(200, 100);
    }

    @GetMapping("/order")
    public void order() {
        Order order = new Order();
        order.setAddress("address here");
        order.setPhone("phone number here");
        order.setName("Artyom");
        OrderItem item = new OrderItem();
        item.setPrice(1000L);
        item.setCount(3);

        order.getOrderItems().add(item);
        Book book = bookRepository.findById(3652L).get();
        item.setBook(book);

        orderRepository.save(order);
    }
}

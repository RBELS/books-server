package com.example.booksserver.rest;

import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.Order;
import com.example.booksserver.entity.OrderItem;
import com.example.booksserver.fillingtest.Filling;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookImageRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public TestController(Filling dbFillComponent, AuthorRepository authorRepository, BookRepository bookRepository, BookImageRepository bookImageRepository, OrderRepository orderRepository) {
        this.dbFillComponent = dbFillComponent;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.bookImageRepository = bookImageRepository;
        this.orderRepository = orderRepository;
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

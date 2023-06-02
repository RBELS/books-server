package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.ResponseStatusWithBodyExceptionFactory;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.Book;
import com.example.booksserver.dto.Order;
import com.example.booksserver.dto.OrderItem;
import com.example.booksserver.dto.Stock;
import com.example.booksserver.entity.BookEntity;
import com.example.booksserver.entity.order.OrderEntity;
import com.example.booksserver.external.IPaymentsRequestService;
import com.example.booksserver.external.PaymentException;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.userstate.CardInfo;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private IPaymentsRequestService paymentsService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ResponseStatusWithBodyExceptionFactory exceptionFactory;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder() throws PaymentException {
        Stock stock = new Stock()
                .setId(30L)
                .setAvailable(10)
                .setOrdered(10)
                .setInDelivery(10);
        Book book = new Book()
                .setStock(stock);
        OrderItem orderItem = new OrderItem()
                .setId(20L)
                .setCount(10)
                .setPrice(new BigDecimal("10.00"))
                .setBook(book);
        Order dto = new Order()
                .setId(10L)
                .setName("Name")
                .setPhone("Some Phone")
                .setEmail("Some Email")
                .setAddress("Some Address")
                .setDateCreated(new Date(System.currentTimeMillis()))
                .setOrderItems(Arrays.asList(
                        orderItem, orderItem
                ));
        CardInfo cardInfo = new CardInfo()
                .setName("Holder name")
                .setNumber("1111222233334444")
                .setCvv("123");

        when(orderMapper.dtoToEntity(any(Order.class)))
                .thenReturn(mock(OrderEntity.class));
        when(orderMapper.entityToDto(any(OrderEntity.class)))
                .thenReturn(dto);
        when(bookRepository.save(any(BookEntity.class)))
                .thenReturn(mock(BookEntity.class));
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(mock(OrderEntity.class));
        when(paymentsService.processPayment(any(Order.class), any(CardInfo.class)))
                .thenReturn(mock(PaymentsInfoResponse.class));
        when(exceptionFactory.create(any(HttpStatus.class), any(ErrorResponseFactory.InternalErrorCode.class)))
                .thenReturn(mock(ResponseBodyException.class));

        assertDoesNotThrow(() -> orderService.createOrder(dto, cardInfo));

        assertThrows(ResponseBodyException.class, () -> orderService.createOrder(
                dto
                        .setName("  "),
                cardInfo
        ));

        assertThrows(ResponseBodyException.class, () -> orderService.createOrder(
                dto
                        .setName("Order Name")
                        .setPhone("   "),
                cardInfo
        ));

        assertThrows(ResponseBodyException.class, () -> orderService.createOrder(
                dto
                        .setPhone("Order phone")
                        .setEmail("   "),
                cardInfo
        ));

        assertThrows(ResponseBodyException.class, () -> orderService.createOrder(
                dto
                        .setEmail("Order email")
                        .setAddress("  "),
                cardInfo
        ));

        assertThrows(ResponseBodyException.class, () -> orderService.createOrder(
                dto
                        .setAddress("Order address")
                        .setOrderItems(
                                Arrays.asList(
                                        orderItem, null
                                )
                        ),
                cardInfo
        ));

        assertThrows(ResponseBodyException.class, () -> orderService.createOrder(
                dto
                        .setOrderItems(new ArrayList<>()),
                cardInfo
        ));

    }
}
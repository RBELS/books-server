package com.example.booksserver.service.impl;

import com.example.booksserver.exception.ErrorResponseFactory;
import com.example.booksserver.exception.InternalErrorCode;
import com.example.booksserver.exception.ResponseBodyException;
import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.service.OrderItem;
import com.example.booksserver.model.service.Stock;
import com.example.booksserver.model.entity.BookEntity;
import com.example.booksserver.model.entity.OrderEntity;
import com.example.booksserver.exception.FailPaymentException;
import com.example.booksserver.rest.PaymentClient;
import com.example.booksserver.exception.PaymentException;
import com.example.booksserver.exception.UnreachablePaymentException;
import com.example.booksserver.model.dto.response.PaymentsInfoResponse;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.service.OrderTransactionService;
import com.example.booksserver.model.service.CardInfo;
import com.example.booksserver.model.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

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
    private PaymentClient paymentsService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ErrorResponseFactory errorResponseFactory;
    @Mock
    private OrderTransactionService orderTransactionService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private static final Stock stock;
    private static final Book book;
    private static final OrderItem orderItem;
    private static final Order dto;
    private static final CardInfo cardInfo;
    static {
        stock = new Stock()
                .setId(30L)
                .setAvailable(10)
                .setOrdered(10)
                .setInDelivery(10);
        book = new Book()
                .setStock(stock);
        orderItem = new OrderItem()
                .setId(20L)
                .setCount(10)
                .setPrice(new BigDecimal("10.00"))
                .setBook(book);
        dto = new Order()
                .setId(10L)
                .setName("Name")
                .setPhone("Some Phone")
                .setEmail("Some Email")
                .setAddress("Some Address")
                .setDateCreated(LocalDateTime.now())
                .setOrderItems(Arrays.asList(
                        orderItem, orderItem
                ));
        cardInfo = new CardInfo()
                .setName("Holder name")
                .setNumber("1111222233334444")
                .setCvv("123");
    }

    @Test
    void createOrder() throws PaymentException {


        when(orderMapper.serviceToEntity(any(Order.class)))
                .thenReturn(mock(OrderEntity.class));
        when(orderMapper.entityToService(any(OrderEntity.class)))
                .thenReturn(dto);
        when(bookRepository.save(any(BookEntity.class)))
                .thenReturn(mock(BookEntity.class));
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(mock(OrderEntity.class));
        when(paymentsService.processPayment(any(Order.class), any(CardInfo.class)))
                .thenReturn(mock(PaymentsInfoResponse.class));
        when(errorResponseFactory.create(any(HttpStatus.class), any(InternalErrorCode.class)))
                .thenReturn(mock(ErrorResponse.class));

        when(orderTransactionService.saveOrder(any(Order.class)))
                .thenReturn(dto);
        when(orderTransactionService.validateAndSetPending(any(Order.class)))
                .thenReturn(dto);

        assertDoesNotThrow(() -> orderService.createOrder(dto, cardInfo));
    }

    @Test
    void cancelOrder() throws UnreachablePaymentException, FailPaymentException {
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(mock(OrderEntity.class));
        when(orderMapper.entityToService(any(OrderEntity.class)))
                .thenReturn(mock(Order.class));
        when(orderMapper.serviceToEntity(any(Order.class)))
                .thenReturn(mock(OrderEntity.class));
        when(errorResponseFactory.create(any(HttpStatus.class), any(InternalErrorCode.class)))
                .thenReturn(mock(ErrorResponse.class));

        when(paymentsService.cancelPayment(anyLong()))
                .thenReturn(mock(PaymentsInfoResponse.class));

        when(orderTransactionService.saveOrder(any(Order.class)))
                .thenReturn(mock(Order.class));

        assertThrows(ResponseBodyException.class, () -> orderService.cancelOrder(dto));
    }
}
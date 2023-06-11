package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.InternalErrorCode;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.Book;
import com.example.booksserver.dto.Order;
import com.example.booksserver.dto.OrderItem;
import com.example.booksserver.dto.Stock;
import com.example.booksserver.entity.BookEntity;
import com.example.booksserver.entity.order.OrderEntity;
import com.example.booksserver.external.FailPaymentException;
import com.example.booksserver.external.IPaymentService;
import com.example.booksserver.external.PaymentException;
import com.example.booksserver.external.UnreachablePaymentException;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.service.IOrderTransactionService;
import com.example.booksserver.userstate.CardInfo;
import com.example.booksserver.userstate.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
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
    private IPaymentService paymentsService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ErrorResponseFactory errorResponseFactory;
    @Mock
    private IOrderTransactionService orderTransactionService;

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
        when(orderMapper.entityToDto(any(OrderEntity.class)))
                .thenReturn(mock(Order.class));
        when(orderMapper.dtoToEntity(any(Order.class)))
                .thenReturn(mock(OrderEntity.class));
        when(errorResponseFactory.create(any(HttpStatus.class), any(InternalErrorCode.class)))
                .thenReturn(mock(ErrorResponse.class));

        when(paymentsService.cancelPayment(anyLong()))
                .thenReturn(mock(PaymentsInfoResponse.class));

        when(orderTransactionService.saveOrder(any(Order.class)))
                .thenReturn(mock(Order.class));

        assertThrows(ResponseBodyException.class, () -> orderService.cancelOrder(10L));
    }

    @Test
    void getOrderById() {
        when(errorResponseFactory.create(any(HttpStatus.class), any(InternalErrorCode.class)))
                .thenReturn(mock(ErrorResponse.class));

        when(orderMapper.entityToDto(any(OrderEntity.class)))
                .thenReturn(mock(Order.class));
        when(orderRepository.findById(10L))
                .thenReturn(Optional.of(mock(OrderEntity.class)));

        assertDoesNotThrow(() -> orderService.getOrderById(10L));
        assertThrows(ResponseBodyException.class, () -> orderService.getOrderById(20L));
    }
}
package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.ResponseStatusWithBodyExceptionFactory;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.dto.OrderItemDTO;
import com.example.booksserver.dto.StockDTO;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.Order;
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
        StockDTO stockDto = new StockDTO()
                .setId(30L)
                .setAvailable(10)
                .setOrdered(10)
                .setInDelivery(10);
        BookDTO bookDTO = new BookDTO()
                .setStock(stockDto);
        OrderItemDTO orderItemDTO = new OrderItemDTO()
                .setId(20L)
                .setCount(10)
                .setPrice(new BigDecimal("10.00"))
                .setBook(bookDTO);
        OrderDTO dto = new OrderDTO()
                .setId(10L)
                .setName("Name")
                .setPhone("Some Phone")
                .setEmail("Some Email")
                .setAddress("Some Address")
                .setDateCreated(new Date(System.currentTimeMillis()))
                .setOrderItems(Arrays.asList(
                        orderItemDTO, orderItemDTO
                ));
        CardInfo cardInfo = new CardInfo()
                .setName("Holder name")
                .setNumber("1111222233334444")
                .setCvv("123");

        when(orderMapper.dtoToEntity(any(OrderDTO.class)))
                .thenReturn(mock(Order.class));
        when(orderMapper.entityToDto(any(Order.class)))
                .thenReturn(dto);
        when(bookRepository.save(any(Book.class)))
                .thenReturn(mock(Book.class));
        when(orderRepository.save(any(Order.class)))
                .thenReturn(mock(Order.class));
        when(paymentsService.processPayment(any(OrderDTO.class), any(CardInfo.class)))
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
                                        orderItemDTO, null
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
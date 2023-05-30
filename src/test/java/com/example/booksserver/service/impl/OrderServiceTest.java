package com.example.booksserver.service.impl;

import com.example.booksserver.components.ResponseStatusWithBodyExceptionFactory;
import com.example.booksserver.external.IPaymentsRequestService;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void createOrder() {


    }
}
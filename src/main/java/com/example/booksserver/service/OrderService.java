package com.example.booksserver.service;

import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.service.CardInfo;
import org.springframework.web.server.ResponseStatusException;

public interface OrderService {
    Order createOrder(Order order, CardInfo cardInfo) throws ResponseStatusException;
    Order cancelOrder(Long orderId) throws ResponseStatusException;
    Order getOrderById(Long orderId) throws ResponseStatusException;
}

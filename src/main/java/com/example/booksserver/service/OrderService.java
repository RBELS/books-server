package com.example.booksserver.service;

import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.service.CardInfo;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order, CardInfo cardInfo) throws ResponseStatusException;
    Order cancelOrder(Order order) throws ResponseStatusException;
    Optional<Order> getOrderById1MinuteAfterCreation(Long orderId);
}

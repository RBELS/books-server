package com.example.booksserver.service;

import com.example.booksserver.dto.Order;
import com.example.booksserver.userstate.CardInfo;
import org.springframework.web.server.ResponseStatusException;

public interface IOrderService {
    Order createOrder(Order order, CardInfo cardInfo) throws ResponseStatusException;
    Order cancelOrder(Order order) throws ResponseStatusException;
    Order getOrderById(Long orderId) throws ResponseStatusException;
}

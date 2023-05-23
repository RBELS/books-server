package com.example.booksserver.service;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.userstate.CardInfo;
import org.springframework.web.server.ResponseStatusException;

public interface IOrderService {
    OrderDTO createOrder(OrderDTO orderDTO, CardInfo cardInfo) throws ResponseStatusException;
}

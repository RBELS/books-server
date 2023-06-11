package com.example.booksserver.service;

import com.example.booksserver.dto.Order;
import org.springframework.web.server.ResponseStatusException;

public interface IOrderTransactionService {
    Order validateAndSetPending(Order order) throws ResponseStatusException;
    Order saveOrder(Order order);
    Order saveOrderReturnStock(Order order);
}

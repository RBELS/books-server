package com.example.booksserver.service;

import com.example.booksserver.model.service.Order;
import org.springframework.web.server.ResponseStatusException;

public interface OrderTransactionService {
    Order validateAndSetPending(Order order) throws ResponseStatusException;
    Order saveOrder(Order order);
    Order saveOrderReturnStock(Order order);
    void updateOrderStock(Order order);
}

package com.example.booksserver.service;

import com.example.booksserver.dto.Order;
import org.springframework.web.server.ResponseStatusException;

public interface IOrderTransactionService {
    Order saveOrder(Order order, boolean doSaveStock);
    Order validateAndSetPending(Order order) throws ResponseStatusException;
}

package com.example.booksserver.service;

import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.service.OrderCancelStatus;
import org.springframework.web.server.ResponseStatusException;

public interface OrderTransactionService {
    Order validateAndSetPending(Order order) throws ResponseStatusException;
    Order saveOrder(Order order);
    Order saveOrderWithNewCancelStatus(Order order);
    Order saveOrderReturnStock(Order order);
    Order saveOrderWithCancelStatusReturnStock(Order order);
    OrderCancelStatus saveOrderCancelStatus(OrderCancelStatus orderCancelStatus);
}

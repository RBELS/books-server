package com.example.booksserver.service;

import com.example.booksserver.model.service.Order;

public interface OrderStatusUpdateService {
    void updateOrderPending(Order order);
    void updateOrderCancelPending(Order order);
    void updateOrderCancelRequest(Order order);
}

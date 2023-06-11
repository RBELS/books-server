package com.example.booksserver.schedule;

import com.example.booksserver.dto.Order;

import java.util.concurrent.CompletableFuture;

public interface IPaymentScheduleTaskService {
    CompletableFuture<Void> updateOrderStatusAsync(Order order);
    void updateOrderStatus(Order order);
}

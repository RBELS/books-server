package com.example.booksserver.schedule;

import com.example.booksserver.model.service.Order;

public interface PaymentScheduleTaskService {
    void updateOrderStatus(Order order);
}

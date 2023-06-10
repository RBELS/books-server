package com.example.booksserver.schedule;

import com.example.booksserver.dto.Order;

public interface IPaymentScheduleTransactionService {
    void saveOrderWithRollback(Order order);
    void updateOrderStatus(Order order);
}

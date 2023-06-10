package com.example.booksserver.schedule;

import com.example.booksserver.dto.Order;
import com.example.booksserver.entity.order.OrderStatus;

public interface IPaymentScheduleTransactionService {
    void saveOrderWithRollback(Order order, OrderStatus resultStatus, boolean doMoveStock);
}

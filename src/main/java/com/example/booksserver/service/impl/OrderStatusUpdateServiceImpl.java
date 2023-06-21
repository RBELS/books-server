package com.example.booksserver.service.impl;

import com.example.booksserver.exception.FailPaymentException;
import com.example.booksserver.exception.UnreachablePaymentException;
import com.example.booksserver.model.dto.response.PaymentsInfoResponse;
import com.example.booksserver.model.entity.OrderStatus;
import com.example.booksserver.model.service.Order;
import com.example.booksserver.rest.PaymentClient;
import com.example.booksserver.service.OrderStatusUpdateService;
import com.example.booksserver.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusUpdateServiceImpl implements OrderStatusUpdateService {
    private final OrderTransactionService orderTransactionService;
    private final PaymentClient paymentClient;

    @Override
    public void updateOrderPending(Order order) {
        PaymentsInfoResponse response;
        try {
            response = paymentClient.getPaymentInfo(order.getId());
        } catch (FailPaymentException e) {
            orderTransactionService.saveOrderReturnStock(order.setStatus(OrderStatus.FAIL));
            return;
        } catch (UnreachablePaymentException e) {
            return;
        }

        switch (response.getStatus()) {
            case "SUCCESS" -> orderTransactionService.saveOrder(order.setStatus(OrderStatus.SUCCESS));
            case "UNSUCCESS" -> orderTransactionService.saveOrderReturnStock(order.setStatus(OrderStatus.FAIL));
            default -> log.error("Payment service returned an unknown status code.");
        }
    }

    @Override
    public void updateOrderPendingCancel(Order order) {
        PaymentsInfoResponse response;
        try {
            response = paymentClient.getPaymentInfo(order.getId());
        } catch (FailPaymentException e) {
            orderTransactionService.saveOrderReturnStock(order.setStatus(OrderStatus.CANCELED));
            return;
        } catch (UnreachablePaymentException e) {
            return;
        }

        switch (response.getStatus()) {
            case "SUCCESS" -> orderTransactionService.saveOrder(order.setStatus(OrderStatus.SUCCESS));
            case "UNSUCCESS", "CANCELED" -> orderTransactionService.saveOrderReturnStock(order.setStatus(OrderStatus.CANCELED));
            default -> log.error("Payment service returned an unknown status code.");
        }
    }
}

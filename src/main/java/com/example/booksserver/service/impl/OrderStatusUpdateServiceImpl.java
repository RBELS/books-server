package com.example.booksserver.service.impl;

import com.example.booksserver.exception.FailPaymentException;
import com.example.booksserver.exception.UnreachablePaymentException;
import com.example.booksserver.model.dto.response.PaymentsInfoResponse;
import com.example.booksserver.model.entity.CancelStatus;
import com.example.booksserver.model.entity.OrderStatus;
import com.example.booksserver.model.service.Order;
import com.example.booksserver.rest.PaymentClient;
import com.example.booksserver.service.OrderStatusUpdateService;
import com.example.booksserver.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    public void updateOrderCancelRequest(Order order) {
        order
                .getOrderCancelStatus()
                .setDateRequested(LocalDateTime.now())
                .setStatus(CancelStatus.PENDING);
        orderTransactionService.saveOrderWithNewCancelStatus(order);

        PaymentsInfoResponse response;
        try {
            response = paymentClient.cancelPayment(order.getId());
        } catch (FailPaymentException e) {
            goBackToRequest(order);
            return;
        } catch (UnreachablePaymentException e) {
            return;
        }

        order = processPaymentInfoResponse(order, response);
    }

    @Override
    public void updateOrderCancelPending(Order order) {
        PaymentsInfoResponse response;
        try {
            response = paymentClient.getPaymentInfo(order.getId());
        } catch (FailPaymentException e) {
            goBackToRequest(order);
            return;
        } catch (UnreachablePaymentException e) {
            return;
        }

        order = processPaymentInfoResponse(order, response);
    }

    private void goBackToRequest(Order order) {
        order
                .getOrderCancelStatus()
                .setDateRequested(null)
                .setStatus(CancelStatus.REQUEST);
        order.setOrderCancelStatus(
                orderTransactionService.saveOrderCancelStatus(order.getOrderCancelStatus())
        );
    }

    private Order processPaymentInfoResponse(Order order, PaymentsInfoResponse response) {
        switch (response.getStatus()) {
            case "SUCCESS", "UNSUCCESS" -> {
                goBackToRequest(order);
            }
            case "CANCELED" -> {
                order.setStatus(OrderStatus.CANCELED);
                order
                        .getOrderCancelStatus()
                        .setStatus(CancelStatus.SUCCESS);
                order = orderTransactionService.saveOrderWithCancelStatusReturnStock(order);
            }
            default -> log.error("Payment service returned an unknown status code.");
        }
        return order;
    }
}

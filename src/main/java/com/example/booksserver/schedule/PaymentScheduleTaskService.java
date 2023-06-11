package com.example.booksserver.schedule;

import com.example.booksserver.dto.Order;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.external.FailPaymentException;
import com.example.booksserver.external.IPaymentService;
import com.example.booksserver.external.UnreachablePaymentException;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.service.IOrderTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduleTaskService implements IPaymentScheduleTaskService {
    private final IPaymentService paymentService;
    private final IOrderTransactionService orderTransactionService;


    // Should I use a static method from the IOrderService class?
    @Async
    @Override
    public CompletableFuture<Void> updateOrderStatusAsync(Order order) {
        PaymentsInfoResponse response;
        try {
            response = paymentService.getPaymentInfo(order.getId());
        } catch (FailPaymentException e) {
            if (OrderStatus.PENDING.equals(order.getStatus())) {
                order.setStatus(OrderStatus.FAIL);
            } else if (OrderStatus.PENDING_CANCEL.equals(order.getStatus())) {
                order.setStatus(OrderStatus.CANCELED);
            } else {
                throw new RuntimeException("Unknown order status.");
            }
            orderTransactionService.saveOrderReturnStock(order);
            return CompletableFuture.completedFuture(null);
        } catch (UnreachablePaymentException e) {
            return CompletableFuture.completedFuture(null);
        }

        switch (response.getStatus()) {
            case "SUCCESS" -> orderTransactionService.saveOrder(order.setStatus(OrderStatus.SUCCESS));
            case "UNSUCCESS" -> orderTransactionService.saveOrderReturnStock(order.setStatus(OrderStatus.FAIL));
            case "CANCELED" -> orderTransactionService.saveOrderReturnStock(order.setStatus(OrderStatus.CANCELED));
            default -> log.error("Payment service returned an unknown status code.");
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void updateOrderStatus(Order order) {
        PaymentsInfoResponse response;
        try {
            response = paymentService.getPaymentInfo(order.getId());
        } catch (FailPaymentException e) {
            if (OrderStatus.PENDING.equals(order.getStatus())) {
                order.setStatus(OrderStatus.FAIL);
            } else if (OrderStatus.PENDING_CANCEL.equals(order.getStatus())) {
                order.setStatus(OrderStatus.CANCELED);
            } else {
                throw new RuntimeException("Unknown order status.");
            }
            orderTransactionService.saveOrderReturnStock(order);
            return;
        } catch (UnreachablePaymentException e) {
            return;
        }

        switch (response.getStatus()) {
            case "SUCCESS" -> orderTransactionService.saveOrder(order.setStatus(OrderStatus.SUCCESS));
            case "UNSUCCESS" -> orderTransactionService.saveOrderReturnStock(order.setStatus(OrderStatus.FAIL));
            case "CANCELED" -> orderTransactionService.saveOrderReturnStock(order.setStatus(OrderStatus.CANCELED));
            default -> log.error("Payment service returned an unknown status code.");
        }

        return;
    }
}

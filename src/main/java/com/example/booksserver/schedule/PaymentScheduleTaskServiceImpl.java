package com.example.booksserver.schedule;

import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.entity.OrderStatus;
import com.example.booksserver.exception.FailPaymentException;
import com.example.booksserver.rest.PaymentClient;
import com.example.booksserver.exception.UnreachablePaymentException;
import com.example.booksserver.model.dto.response.PaymentsInfoResponse;
import com.example.booksserver.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduleTaskServiceImpl implements PaymentScheduleTaskService {
    private final PaymentClient paymentService;
    private final OrderTransactionService orderTransactionService;

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
    }
}

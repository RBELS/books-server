package com.example.booksserver.schedule;

import com.example.booksserver.exception.FailPaymentException;
import com.example.booksserver.exception.UnreachablePaymentException;
import com.example.booksserver.model.dto.response.PaymentsInfoResponse;
import com.example.booksserver.model.entity.OrderStatus;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.model.service.Order;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.rest.PaymentClient;
import com.example.booksserver.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentScheduleExecutor {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentClient paymentClient;
    private final OrderTransactionService orderTransactionService;

    private LocalDateTime getDateTime1DayBefore() {
        return LocalDateTime.now().minusDays(1);
    }

    @Scheduled(fixedDelayString = "${spring.task.scheduling.cron.pending}")
    @Transactional
    public void updatePendingOrders() {
        orderRepository
                .findAllByStatusAndDateCreatedAfter(OrderStatus.PENDING, getDateTime1DayBefore())
                .stream().map(orderMapper::entityToService)
                .forEach(this::updateOrderPending);
    }

    @Scheduled(fixedDelayString = "${spring.task.scheduling.cron.pending-cancel}")
    @Transactional
    public void updatePendingCancelOrders() {
        orderRepository
                .findAllByStatusAndDateCreatedAfter(OrderStatus.PENDING_CANCEL, getDateTime1DayBefore())
                .stream().map(orderMapper::entityToService)
                .forEach(this::updateOrderPendingCancel);
    }

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

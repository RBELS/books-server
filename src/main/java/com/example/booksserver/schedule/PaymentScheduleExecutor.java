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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentScheduleExecutor {
    public static final List<OrderStatus> pendingStatusList = Arrays.asList(OrderStatus.PENDING, OrderStatus.PENDING_CANCEL);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentClient paymentClient;
    private final OrderTransactionService orderTransactionService;


    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelay = 1)
    @Transactional
    public void updateOrderStatuses() {
        orderRepository
                .findAllByStatusIn(
                        pendingStatusList
                )
                .stream().map(orderMapper::entityToDto)
                .forEach(this::updateOrderStatus);
    }

    public void updateOrderStatus(Order order) {
        PaymentsInfoResponse response;
        try {
            response = paymentClient.getPaymentInfo(order.getId());
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

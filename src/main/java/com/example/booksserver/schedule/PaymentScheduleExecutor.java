package com.example.booksserver.schedule;

import com.example.booksserver.dto.Order;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.external.FailPaymentException;
import com.example.booksserver.external.IPaymentService;
import com.example.booksserver.external.UnreachablePaymentException;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
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
    private final IPaymentScheduleTransactionService paymentScheduleTransactionService;
    private final IPaymentService paymentService;


    // Should I try to catch any exception, but continue
    // sending requests?
    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 1)
    @Transactional
    public void updateOrderStatuses() {
        orderRepository
                .findAllByStatusIn(
                        pendingStatusList
                )
                .stream().map(orderMapper::entityToDto)
                .forEach(this::updateOrderStatus);
    }

    private void updateOrderStatus(Order order) {
        PaymentsInfoResponse response;
        try {
            response = paymentService.getPaymentInfo(order.getId());
        } catch (FailPaymentException e) {
            OrderStatus resultStatus;
            if (OrderStatus.PENDING.equals(order.getStatus())) {
                resultStatus = OrderStatus.FAIL;
            } else if (OrderStatus.PENDING_CANCEL.equals(order.getStatus())) {
                resultStatus = OrderStatus.CANCELED;
            } else {
                throw new RuntimeException("Unknown order status.");
            }
            paymentScheduleTransactionService.saveOrderWithRollback(order, resultStatus, true);
            return;
        } catch (UnreachablePaymentException e) {
            return;
        }

        switch (response.getStatus()) {
            case "SUCCESS" -> paymentScheduleTransactionService.saveOrderWithRollback(order, OrderStatus.SUCCESS, false);
            case "UNSUCCESS" -> paymentScheduleTransactionService.saveOrderWithRollback(order, OrderStatus.FAIL, true);
            case "CANCELED" -> paymentScheduleTransactionService.saveOrderWithRollback(order, OrderStatus.CANCELED, true);
            default -> log.error("Payment service returned an unknown status code.");
        }
    }


}

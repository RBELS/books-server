package com.example.booksserver.schedule;

import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PaymentScheduleExecutor {
    public static final List<OrderStatus> pendingStatusList = Arrays.asList(OrderStatus.PENDING, OrderStatus.PENDING_CANCEL);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final IPaymentScheduleTransactionService paymentScheduleTransactionService;


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
                .forEach(paymentScheduleTransactionService::updateOrderStatus);
    }


}

package com.example.booksserver.schedule;

import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentScheduleExecutor {
    public static final List<OrderStatus> pendingStatusList = Arrays.asList(OrderStatus.PENDING, OrderStatus.PENDING_CANCEL);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final IPaymentScheduleTaskService paymentScheduleTaskService;


    // Should I try to catch any exception, but continue
    // sending requests?
    @Transactional
    public void updateOrderStatusesAsync() {
        List<CompletableFuture<?>> futureList = new ArrayList<>();
        orderRepository
                .findAllByStatusIn(
                        pendingStatusList
                )
                .stream().map(orderMapper::entityToDto)
                .forEach(order -> futureList.add(paymentScheduleTaskService.updateOrderStatusAsync(order)));

        futureList.forEach(completableFuture -> {
            try {
                completableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelay = 1)
    @Transactional
    public void updateOrderStatuses() {
        orderRepository
                .findAllByStatusIn(
                        pendingStatusList
                )
                .stream().map(orderMapper::entityToDto)
                .forEach(paymentScheduleTaskService::updateOrderStatus);
    }

}

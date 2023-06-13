package com.example.booksserver.schedule;

import com.example.booksserver.model.entity.OrderStatus;
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
    private final PaymentScheduleTaskService paymentScheduleTaskService;

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

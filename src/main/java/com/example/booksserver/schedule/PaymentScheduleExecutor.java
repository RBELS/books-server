package com.example.booksserver.schedule;

import com.example.booksserver.model.entity.OrderStatus;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.service.OrderStatusUpdateService;
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
    private final OrderStatusUpdateService orderStatusUpdateService;

    private LocalDateTime getDateTime1DayBefore() {
        return LocalDateTime.now().minusDays(1);
    }

    @Scheduled(fixedDelayString = "${spring.task.scheduling.delay.pending}")
    @Transactional
    public void updatePendingOrders() {
        orderRepository
                .findAllByStatusAndDateCreatedAfter(OrderStatus.PENDING, getDateTime1DayBefore())
                .stream().map(orderMapper::entityToService)
                .forEach(orderStatusUpdateService::updateOrderPending);
    }

    @Scheduled(fixedDelayString = "${spring.task.scheduling.delay.pending-cancel}")
    @Transactional
    public void updatePendingCancelOrders() {
        orderRepository
                .findAllByStatusAndDateCreatedAfter(OrderStatus.PENDING_CANCEL, getDateTime1DayBefore())
                .stream().map(orderMapper::entityToService)
                .forEach(orderStatusUpdateService::updateOrderPendingCancel);
    }
}

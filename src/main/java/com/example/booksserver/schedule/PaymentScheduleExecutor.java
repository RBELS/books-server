package com.example.booksserver.schedule;

import com.example.booksserver.dto.Order;
import com.example.booksserver.dto.Stock;
import com.example.booksserver.entity.order.OrderEntity;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.external.FailPaymentException;
import com.example.booksserver.external.IPaymentsRequestService;
import com.example.booksserver.external.UnreachablePaymentException;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PaymentScheduleExecutor {
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final OrderMapper orderMapper;
    private final IPaymentsRequestService paymentsService;


    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 5)
    @Transactional(noRollbackFor = Exception.class)
    public void updateOrderStatuses() {
        orderRepository
                .findAllByStatusIn(
                        Arrays.asList(OrderStatus.PENDING, OrderStatus.PENDING_CANCEL)
                )
                .stream().map(orderMapper::entityToDto)
                .forEach(this::updateOrderStatus);
    }

    private void saveOrderWithRollback(Order order) {
        order.getOrderItems().forEach(orderItemDTO -> {
            int orderCount = orderItemDTO.getCount();
            Stock stock = orderItemDTO.getBook().getStock();
            stock.setAvailable(stock.getAvailable() + orderCount);
            stock.setInDelivery(stock.getInDelivery() - orderCount);
        });

        OrderEntity orderEntity = orderMapper.dtoToEntity(order);
        orderEntity.getOrderItems().forEach(orderItem -> stockRepository.save(orderItem.getBook().getStock()));
        orderRepository.save(orderEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateOrderStatus(Order order) {
        PaymentsInfoResponse response = null;
        try {
            response = paymentsService.getPaymentInfo(order.getId());
        } catch (FailPaymentException e) {
            if (OrderStatus.PENDING.equals(order.getStatus())) {
                order.setStatus(OrderStatus.FAIL);
            } else if (OrderStatus.PENDING_CANCEL.equals(order.getStatus())) {
                order.setStatus(OrderStatus.CANCELED);
            }
            saveOrderWithRollback(order);
            return;
        } catch (UnreachablePaymentException e) {
            return;
        }

        if ("SUCCESS".equals(response.getStatus())) {
            order.setStatus(OrderStatus.SUCCESS);
            OrderEntity orderEntity = orderMapper.dtoToEntity(order);
            orderRepository.save(orderEntity);
        } else if ("UNSUCCESS".equals(response.getStatus())) {
            order.setStatus(OrderStatus.FAIL);
            saveOrderWithRollback(order);
        } else if ("CANCELED".equals(response.getStatus())) {
            order.setStatus(OrderStatus.CANCELED);
            saveOrderWithRollback(order);
        }
    }
}

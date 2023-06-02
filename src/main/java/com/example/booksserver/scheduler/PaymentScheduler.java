package com.example.booksserver.scheduler;

import com.example.booksserver.dto.Order;
import com.example.booksserver.dto.Stock;
import com.example.booksserver.entity.order.OrderEntity;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.external.IPaymentsRequestService;
import com.example.booksserver.external.PaymentException;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final OrderMapper orderMapper;
    private final IPaymentsRequestService paymentsService;


    // Transactional added to start other transactions within it
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 5)
    // @Transactional(noRollbackFor = Exception.class)
    @Transactional
    public void updateOrderStatuses() {
        orderRepository
                .findAllByStatus(OrderStatus.PENDING)
                .stream().map(orderMapper::entityToDto)
                .forEach(this::updateOrderStatus);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateOrderStatus(Order orderDTO) {
        PaymentsInfoResponse response;
        try {
            response = paymentsService.getPaymentInfo(orderDTO.getId());
        } catch (PaymentException e) {
            if (OrderStatus.FAIL.equals(e.getStatus())) {
                orderDTO.setStatus(OrderStatus.FAIL);
                orderDTO.getOrderItems().forEach(orderItemDTO -> {
                    int orderCount = orderItemDTO.getCount();
                    Stock stock = orderItemDTO.getBook().getStock();
                    stock.setAvailable(stock.getAvailable() + orderCount);
                    stock.setInDelivery(stock.getInDelivery() - orderCount);
                });

                OrderEntity orderEntity = orderMapper.dtoToEntity(orderDTO);
                orderEntity.getOrderItems().forEach(orderItem -> stockRepository.save(orderItem.getBook().getStock()));
                orderRepository.save(orderEntity);
            }
            return;
        }

        if ("SUCCESS".equals(response.getStatus())) {
            orderDTO.setStatus(OrderStatus.SUCCESS);
            OrderEntity orderEntity = orderMapper.dtoToEntity(orderDTO);
            orderRepository.save(orderEntity);
        }
    }
}

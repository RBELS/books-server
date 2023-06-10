package com.example.booksserver.schedule;

import com.example.booksserver.dto.Order;
import com.example.booksserver.dto.Stock;
import com.example.booksserver.entity.order.OrderEntity;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.external.FailPaymentException;
import com.example.booksserver.external.IPaymentService;
import com.example.booksserver.external.UnreachablePaymentException;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduleTransactionService implements IPaymentScheduleTransactionService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final IPaymentService paymentService;


    // Should I use a static method from the IOrderService class?
    @Override
    public void saveOrderWithRollback(Order order) {
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

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    @Override
    public void updateOrderStatus(Order order) {
        order = orderMapper.entityToDto(
                orderRepository.findById(order.getId())
                        .orElseThrow(() -> new RuntimeException("Order not found when updating statuses."))
        );

        if (!PaymentScheduleExecutor.pendingStatusList.contains(order.getStatus())) {
            return;
        }

        PaymentsInfoResponse response;
        try {
            response = paymentService.getPaymentInfo(order.getId());
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


        switch (response.getStatus()) {
            case "SUCCESS" -> {
                order.setStatus(OrderStatus.SUCCESS);
                OrderEntity orderEntity = orderMapper.dtoToEntity(order);
                orderRepository.save(orderEntity);
            }
            case "UNSUCCESS" -> {
                order.setStatus(OrderStatus.FAIL);
                saveOrderWithRollback(order);
            }
            case "CANCELED" -> {
                order.setStatus(OrderStatus.CANCELED);
                saveOrderWithRollback(order);
            }
            default -> log.error("Payment service returned an unknown status code.");
        }
    }
}

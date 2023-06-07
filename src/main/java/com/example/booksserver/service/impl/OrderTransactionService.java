package com.example.booksserver.service.impl;

import com.example.booksserver.dto.Order;
import com.example.booksserver.entity.order.OrderEntity;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.external.FailPaymentException;
import com.example.booksserver.external.UnreachablePaymentException;
import com.example.booksserver.external.impl.PaymentService;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.repository.StockRepository;
import com.example.booksserver.service.IOrderTransactionService;
import com.example.booksserver.userstate.CardInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class OrderTransactionService implements IOrderTransactionService {
    private final OrderMapper orderMapper;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public Order saveOrder(Order order, boolean doSaveStock) {
        OrderEntity orderEntity = orderMapper.dtoToEntity(order);
        if (doSaveStock) {
            orderEntity
                    .getOrderItems()
                    .forEach(orderItem -> stockRepository.save(orderItem.getBook().getStock()));
        }
        return orderMapper.entityToDto(
                orderRepository.save(orderEntity)
        );
    }

    public Order processPayment(Order order, CardInfo cardInfo) {
        try {
            paymentService.processPayment(order, cardInfo);
            order.setStatus(OrderStatus.SUCCESS);
        } catch (FailPaymentException e) {
            order.setStatus(OrderStatus.FAIL);
        } catch (UnreachablePaymentException e) {
            order.setStatus(OrderStatus.PENDING);
        }
        return order;
    }

    public Order processCancelPayment(Order order) {
        try {
            paymentService.cancelPayment(order.getId());
            order.setStatus(OrderStatus.CANCELED);
        } catch (FailPaymentException e) {
            //for the situation when the payment was not process, Order is PENDING
            order.setStatus(OrderStatus.CANCELED);
        } catch (UnreachablePaymentException e) {
            order.setStatus(OrderStatus.PENDING_CANCEL);
        }
        return order;
    }
}

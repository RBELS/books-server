package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.InternalErrorCode;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.Order;
import com.example.booksserver.entity.order.OrderEntity;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.external.FailPaymentException;
import com.example.booksserver.external.UnreachablePaymentException;
import com.example.booksserver.external.impl.PaymentService;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.repository.StockRepository;
import com.example.booksserver.service.IOrderService;
import com.example.booksserver.service.IOrderTransactionService;
import com.example.booksserver.userstate.CardInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderTransactionService implements IOrderTransactionService {
    private final OrderMapper orderMapper;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final ErrorResponseFactory errorResponseFactory;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
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

    @Override
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

    @Override
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

    private void validateOrder(Order order) throws ResponseStatusException {
        if (order.getEmail().isBlank()) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.ORDER_BAD_EMAIL)
            );
        } else if (order.getAddress().isBlank()) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.ORDER_BAD_ADDRESS)
            );
        } else if (order.getName().isBlank()) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.ORDER_BAD_NAME)
            );
        } else if (order.getPhone().isBlank()) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.ORDER_BAD_PHONE)
            );
        } else if (order.getOrderItems().isEmpty()) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.ORDER_NO_ITEMS)
            );
        } else if (order.getOrderItems().contains(null)) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.ORDER_ITEM_NOT_FOUND)
            );
        }

        order.getOrderItems().forEach(orderItemDto -> {
            int availableBooks = orderItemDto.getBook().getStock().getAvailable();
            int orderBooks = orderItemDto.getCount();
            if (orderBooks > availableBooks) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                throw new ResponseBodyException(status,
                        errorResponseFactory.create(status, InternalErrorCode.ORDER_ITEM_NOT_IN_STOCK)
                );
            }
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = {ResponseBodyException.class})
    public Order validateAndSetPending(Order order) throws ResponseStatusException {
        validateOrder(order);
        order.setStatus(OrderStatus.PENDING);
        IOrderService.moveStock(order, false);
        order = saveOrder(order, true);
        return order;
    }
}

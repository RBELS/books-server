package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.InternalErrorCode;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.Order;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.service.IOrderService;
import com.example.booksserver.service.IOrderTransactionService;
import com.example.booksserver.userstate.CardInfo;
import com.example.booksserver.userstate.response.CancelOrderResponse;
import com.example.booksserver.userstate.response.PostOrdersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ErrorResponseFactory errorResponseFactory;
    private final IOrderTransactionService orderTransactionService;

    @Override
    public Order createOrder(Order order, CardInfo cardInfo) throws ResponseStatusException {
        order = orderTransactionService.validateAndSetPending(order);

        // may be moved to OrderService class
        order = orderTransactionService.processPayment(order, cardInfo);

        if (OrderStatus.FAIL.equals(order.getStatus())) {
            IOrderService.moveStock(order, true);
            order = orderTransactionService.saveOrder(order, true);
        } else {
            order = orderTransactionService.saveOrder(order, false);
        }

        if (OrderStatus.FAIL.equals(order.getStatus())) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.PAYMENT_ERROR)
            );
        } else if (OrderStatus.PENDING.equals(order.getStatus())) {
            throw new ResponseBodyException(HttpStatus.OK, new PostOrdersResponse(order, "PENDING"));
        }

        return order;
    }

    @Override
    public Order cancelOrder(Order order) throws ResponseStatusException {
        if (!OrderStatus.SUCCESS.equals(order.getStatus()) && !OrderStatus.PENDING.equals(order.getStatus())) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.PAYMENT_CANCEL_NOT_ALLOWED)
            );
        }

        order.setStatus(OrderStatus.PENDING_CANCEL);
        order = orderTransactionService.saveOrder(order, false);

        // may be moved to OrderService class
        order = orderTransactionService.processCancelPayment(order);

        if (OrderStatus.PENDING_CANCEL.equals(order.getStatus())) {
            throw new ResponseBodyException(HttpStatus.OK, new CancelOrderResponse()
                    .setOrderNo(String.valueOf(order.getId()))
                    .setStatus("PENDING_CANCEL")
            );
        }

        IOrderService.moveStock(order, true);
        order = orderTransactionService.saveOrder(order, true);

        return order;
    }

    @Override
    @Transactional
    public Order getOrderById(Long orderId) throws ResponseStatusException {
        return orderRepository
                .findById(orderId)
                .map(orderMapper::entityToDto)
                .orElseThrow(() -> {
                    HttpStatus status = HttpStatus.NOT_FOUND;
                    return new ResponseBodyException(status,
                            errorResponseFactory.create(status, InternalErrorCode.ORDER_NOT_FOUND)
                    );
                });
    }
}

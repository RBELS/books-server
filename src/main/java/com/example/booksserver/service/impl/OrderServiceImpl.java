package com.example.booksserver.service.impl;

import com.example.booksserver.exception.ErrorResponseFactory;
import com.example.booksserver.exception.InternalErrorCode;
import com.example.booksserver.exception.ResponseBodyException;
import com.example.booksserver.model.entity.CancelStatus;
import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.entity.OrderStatus;
import com.example.booksserver.exception.FailPaymentException;
import com.example.booksserver.model.service.OrderCancelStatus;
import com.example.booksserver.rest.PaymentClient;
import com.example.booksserver.exception.UnreachablePaymentException;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.service.OrderService;
import com.example.booksserver.service.OrderTransactionService;
import com.example.booksserver.model.service.CardInfo;
import com.example.booksserver.model.dto.response.PostOrdersResponse;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ErrorResponseFactory errorResponseFactory;
    private final OrderTransactionService orderTransactionService;
    private final PaymentClient paymentClient;

    @Override
    public Order createOrder(Order order, CardInfo cardInfo) throws ResponseStatusException {
        order = orderTransactionService.validateAndSetPending(order);

        try {
            paymentClient.processPayment(order, cardInfo);
            order.setStatus(OrderStatus.SUCCESS);
            order = orderTransactionService.saveOrder(order);
        } catch (FailPaymentException e) {
            order.setStatus(OrderStatus.FAIL);
            order = orderTransactionService.saveOrderReturnStock(order);
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.PAYMENT_ERROR)
            );
        } catch (UnreachablePaymentException e) {
            order.setStatus(OrderStatus.PENDING);
            order = orderTransactionService.saveOrder(order);
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

        try {
            order = orderTransactionService.saveOrderWithNewCancelStatus(
                    order
                            .setOrderCancelStatus(new OrderCancelStatus()
                                    .setStatus(CancelStatus.REQUEST)
                            )
            );
        } catch (EntityExistsException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.PAYMENT_CANCEL_NOT_ALLOWED)
            );
        }

        return order;
    }

    @Override
    public Optional<Order> getOrderById1MinuteAfterCreation(Long orderId) {
        return orderRepository
                .findByIdAndDateCreatedBefore(orderId, LocalDateTime.now().minusMinutes(1))
                .map(orderMapper::entityToService);
    }
}

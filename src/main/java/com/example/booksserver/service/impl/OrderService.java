package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.InternalErrorCode;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.Order;
import com.example.booksserver.dto.Stock;
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

    private void moveStock(Order order, boolean isReverse) {
        order.getOrderItems().forEach(orderItemDto -> {
            Stock stock = orderItemDto.getBook().getStock();
            int moveModifier = isReverse ? -1 : 1;
            stock.setAvailable(stock.getAvailable() - moveModifier * orderItemDto.getCount());
            stock.setInDelivery(stock.getInDelivery() + moveModifier * orderItemDto.getCount());
        });
    }

    @Override
    public Order createOrder(Order order, CardInfo cardInfo) throws ResponseStatusException {
        validateOrder(order);

        order.setStatus(OrderStatus.PENDING);
        moveStock(order, false);

        order = orderTransactionService.saveOrder(order, true);
        order = orderTransactionService.processPayment(order, cardInfo);

        if (OrderStatus.FAIL.equals(order.getStatus())) {
            moveStock(order, true);
        }

        order = orderTransactionService.saveOrder(order, true);

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

        order = orderTransactionService.processCancelPayment(order);

        if (OrderStatus.PENDING_CANCEL.equals(order.getStatus())) {
            throw new ResponseBodyException(HttpStatus.OK, new CancelOrderResponse()
                    .setOrderNo(String.valueOf(order.getId()))
                    .setStatus("PENDING_CANCEL")
            );
        }

        moveStock(order, true);
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

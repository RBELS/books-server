package com.example.booksserver.service.impl;

import com.example.booksserver.exception.*;
import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.entity.OrderEntity;
import com.example.booksserver.model.entity.OrderStatus;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.map.StockMapper;
import com.example.booksserver.model.service.Stock;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.repository.StockRepository;
import com.example.booksserver.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderTransactionServiceImpl implements OrderTransactionService {
    private final OrderMapper orderMapper;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final ErrorResponseFactory errorResponseFactory;
    private final StockMapper stockMapper;

    public void moveAndSaveStock(Order order, boolean isReverse) {
        order.getOrderItems().forEach(orderItem -> {
            Stock stock = orderItem.getBook().getStock();
            int moveMultiplier = isReverse ? -1 : 1;
            stock.setAvailable(stock.getAvailable() - moveMultiplier * orderItem.getCount());
            stock.setInDelivery(stock.getInDelivery() + moveMultiplier * orderItem.getCount());
            orderItem.getBook().setStock(
                    stockMapper.entityToService(
                            stockRepository.save(
                                    stockMapper.serviceToEntity(stock)
                            )
                    )
            );
        });
    }

    @Override
    public void updateOrderStock(Order order) {
        order.getOrderItems().forEach(orderItem -> {
            Stock oldStock = orderItem.getBook().getStock();
            orderItem.getBook().setStock(
                    stockRepository
                            .findByIdLocked(oldStock.getId())
                            .map(stockMapper::entityToService)
                            .orElseThrow()
            );
        });
    }

    public void validateOrder(Order order) throws ResponseStatusException {
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

        order.getOrderItems().forEach(orderItem -> {
            int availableBooks = orderItem.getBook().getStock().getAvailable();
            int orderBooks = orderItem.getCount();
            if (orderBooks > availableBooks) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                throw new ResponseBodyException(status,
                        errorResponseFactory.create(status, InternalErrorCode.ORDER_ITEM_NOT_IN_STOCK)
                );
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order validateAndSetPending(Order order) throws ResponseStatusException {
        updateOrderStock(order);
        validateOrder(order);
        order.setStatus(OrderStatus.PENDING);
        moveAndSaveStock(order, false);
        return orderMapper.entityToService(
                orderRepository.save(
                        orderMapper.serviceToEntity(order)
                )
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order saveOrder(Order order) {
        OrderEntity orderEntity = orderMapper.serviceToEntity(order);
        orderEntity = orderRepository.save(orderEntity);
        return orderMapper.entityToService(orderEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order saveOrderReturnStock(Order order) {
        orderRepository.findByIdLocked(order.getId());
        updateOrderStock(order);
        moveAndSaveStock(order, true);
        OrderEntity orderEntity = orderMapper.serviceToEntity(order);
        orderEntity = orderRepository.save(orderEntity);
        return orderMapper.entityToService(orderEntity);
    }
}

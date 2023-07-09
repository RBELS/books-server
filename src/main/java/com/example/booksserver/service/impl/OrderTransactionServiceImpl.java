package com.example.booksserver.service.impl;

import com.example.booksserver.exception.*;
import com.example.booksserver.map.OrderCancelStatusMapper;
import com.example.booksserver.model.entity.OrderCancelStatusEntity;
import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.entity.OrderEntity;
import com.example.booksserver.model.entity.OrderStatus;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.model.service.OrderCancelStatus;
import com.example.booksserver.model.service.Stock;
import com.example.booksserver.repository.OrderCancelStatusRepository;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.repository.StockRepository;
import com.example.booksserver.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderTransactionServiceImpl implements OrderTransactionService {
    private final OrderMapper orderMapper;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final ErrorResponseFactory errorResponseFactory;
    private final OrderCancelStatusRepository orderCancelStatusRepository;
    private final OrderCancelStatusMapper orderCancelStatusMapper;

    public void moveAndSaveStock(Order order, boolean isReverse) {
        order.getOrderItems().forEach(orderItem -> {
            Stock stock = orderItem.getBook().getStock();
            Integer updatedStockEntityCount;
            if (!isReverse) {
                updatedStockEntityCount = stockRepository.updateStockMoveAvailable(stock.getId(), orderItem.getCount());
            } else {
                updatedStockEntityCount = stockRepository.updateStockMoveInDelivery(stock.getId(), orderItem.getCount());
            }

            if (updatedStockEntityCount == 0) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                throw new ResponseBodyException(status,
                        errorResponseFactory.create(status, InternalErrorCode.ORDER_ITEM_NOT_IN_STOCK)
                );
            }
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
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order validateAndSetPending(Order order) throws ResponseStatusException {
        validateOrder(order); // stock items are not validated here, not in stock exception is thrown with `moveAndSaveStock`
        order.setStatus(OrderStatus.PENDING);
        moveAndSaveStock(order, false); //update query
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
    public Order saveOrderWithNewCancelStatus(Order order) {
        OrderCancelStatusEntity orderCancelStatusEntity = orderCancelStatusMapper
                .serviceToEntity(order.getOrderCancelStatus())
                .setOrder(orderRepository.findById(order.getId()).orElseThrow());

        orderCancelStatusEntity = orderCancelStatusRepository.save(
                orderCancelStatusEntity
        );

        order.setOrderCancelStatus(
                orderCancelStatusMapper.entityToService(
                        orderCancelStatusEntity
                )
        );

        order = saveOrder(order);
        return order;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order saveOrderReturnStock(Order order) {
        moveAndSaveStock(order, true);
        OrderEntity orderEntity = orderMapper.serviceToEntity(order);
        orderEntity = orderRepository.save(orderEntity);
        return orderMapper.entityToService(orderEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order saveOrderWithCancelStatusReturnStock(Order order) {
        orderCancelStatusRepository.save(
                orderCancelStatusMapper.serviceToEntity(order.getOrderCancelStatus())
        );
        moveAndSaveStock(order, true);

        return orderMapper.entityToService(
                orderRepository.save(
                        orderMapper.serviceToEntity(order)
                )
        );
    }

    @Override
    public OrderCancelStatus saveOrderCancelStatus(OrderCancelStatus orderCancelStatus) {
        return orderCancelStatusMapper.entityToService(
                orderCancelStatusRepository.save(
                        orderCancelStatusMapper.serviceToEntity(orderCancelStatus)
                )
        );
    }

    @Override
    public void deleteOrderCancelStatus(Order order) {
        if (!Objects.isNull(order.getOrderCancelStatus())) {
            orderCancelStatusRepository.deleteById(order.getOrderCancelStatus().getId());
            order.setOrderCancelStatus(null);
        }
    }
}

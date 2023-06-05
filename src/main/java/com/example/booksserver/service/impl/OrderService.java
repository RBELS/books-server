package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.InternalErrorCode;
import com.example.booksserver.config.ResponseBodyException;
import com.example.booksserver.dto.Order;
import com.example.booksserver.dto.Stock;
import com.example.booksserver.entity.order.OrderEntity;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.external.FailPaymentException;
import com.example.booksserver.external.UnreachablePaymentException;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.external.IPaymentsRequestService;
import com.example.booksserver.repository.StockRepository;
import com.example.booksserver.service.IOrderService;
import com.example.booksserver.userstate.CardInfo;
import com.example.booksserver.userstate.response.CancelOrderResponse;
import com.example.booksserver.userstate.response.PostOrdersResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final IPaymentsRequestService paymentsService;
    private final StockRepository stockRepository;
    private final ErrorResponseFactory errorResponseFactory;

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
    @Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = {ResponseStatusException.class})
    public Order createOrder(Order order, CardInfo cardInfo) throws ResponseStatusException {
        validateOrder(order);
        order = saveOrderPending(order);
        order = processPayment(order, cardInfo);
        return order;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order saveOrderPending(Order order) {
        order.setStatus(OrderStatus.PENDING);
        order.getOrderItems().forEach(orderItemDto -> {
            Stock stock = orderItemDto.getBook().getStock();
            stock.setAvailable(stock.getAvailable() - orderItemDto.getCount());
            stock.setInDelivery(stock.getInDelivery() + orderItemDto.getCount());
        });

        OrderEntity orderEntity = orderMapper.dtoToEntity(order);
        orderEntity
                .getOrderItems()
                .forEach(orderItem -> stockRepository.save(orderItem.getBook().getStock()));

        return orderMapper.entityToDto(
                orderRepository.save(orderEntity)
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = {ResponseStatusException.class})
    public Order processPayment(Order order, CardInfo cardInfo) throws ResponseStatusException {
        try {
            paymentsService.processPayment(order, cardInfo);
            order.setStatus(OrderStatus.SUCCESS);
        } catch (FailPaymentException e) {
            order.setStatus(OrderStatus.FAIL);
        } catch (UnreachablePaymentException e) {
            order.setStatus(OrderStatus.PENDING);
        }

        if (OrderStatus.FAIL.equals(order.getStatus())) {
            order.getOrderItems().forEach(orderItemDto -> {
                Stock stock = orderItemDto.getBook().getStock();
                stock.setAvailable(stock.getAvailable() + orderItemDto.getCount());
                stock.setInDelivery(stock.getInDelivery() - orderItemDto.getCount());
            });
        }

        OrderEntity orderEntity = orderMapper.dtoToEntity(order);
        orderEntity.getOrderItems().forEach(orderItem -> stockRepository.save(orderItem.getBook().getStock()));
        orderRepository.save(orderEntity);

        order = orderMapper.entityToDto(orderEntity);

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
    @Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = {ResponseStatusException.class})
    public Order cancelOrder(Order order) throws ResponseStatusException {
        if (!order.getStatus().equals(OrderStatus.SUCCESS) && !order.getStatus().equals(OrderStatus.PENDING)) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.PAYMENT_CANCEL_NOT_ALLOWED)
            );
        }

        order = saveOrderCancelPending(order);
        order = processCancelPayment(order);
        return order;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order saveOrderCancelPending(Order order) {
        order.setStatus(OrderStatus.PENDING_CANCEL);

        OrderEntity orderEntity = orderMapper.dtoToEntity(order);
        return orderMapper.entityToDto(
                orderRepository.save(orderEntity)
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = {ResponseStatusException.class})
    public Order processCancelPayment(Order order) throws ResponseStatusException {
        try {
            paymentsService.cancelPayment(order.getId());
            order.setStatus(OrderStatus.CANCELED);
        } catch (FailPaymentException e) {
            order.setStatus(OrderStatus.FAIL);// ?????? SHOULD IT BE?!!!!!!!!!!!!!!!!!!!!!!!!!!!
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseBodyException(status,
                    errorResponseFactory.create(status, InternalErrorCode.PAYMENT_ERROR)
            );
        } catch (UnreachablePaymentException e) {
            order.setStatus(OrderStatus.PENDING_CANCEL);
            throw new ResponseBodyException(HttpStatus.OK, new CancelOrderResponse()
                    .setOrderNo(String.valueOf(order.getId()))
                    .setStatus("PENDING_CANCEL")
            );
        }

        order.getOrderItems().forEach(orderItem -> {
            Stock stock = orderItem.getBook().getStock();
            stock.setAvailable(stock.getAvailable() + orderItem.getCount());
            stock.setInDelivery(stock.getInDelivery() - orderItem.getCount());
        });
        OrderEntity orderEntity = orderMapper.dtoToEntity(order);
        orderEntity = orderRepository.save(orderEntity);
        order = orderMapper.entityToDto(orderEntity);

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

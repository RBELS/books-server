package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.ResponseStatusWithBodyExceptionFactory;
import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.dto.StockDTO;
import com.example.booksserver.entity.Order;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.external.IPaymentsRequestService;
import com.example.booksserver.external.PaymentException;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.external.response.PaymentsErrorResponse;
import com.example.booksserver.service.IOrderService;
import com.example.booksserver.userstate.CardInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final IPaymentsRequestService paymentsService;
    private final BookRepository bookRepository;
    private final ResponseStatusWithBodyExceptionFactory exceptionFactory;

    public OrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            IPaymentsRequestService paymentsService,
            BookRepository bookRepository,
            ResponseStatusWithBodyExceptionFactory exceptionFactory
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.paymentsService = paymentsService;
        this.bookRepository = bookRepository;
        this.exceptionFactory = exceptionFactory;
    }

    private void validateOrder(OrderDTO orderDTO) throws ResponseStatusException {
        if (orderDTO.getEmail().isBlank()) {
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.ORDER_BAD_EMAIL);
        } else if (orderDTO.getAddress().isBlank()) {
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.ORDER_BAD_ADDRESS);
        } else if (orderDTO.getName().isBlank()) {
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.ORDER_BAD_NAME);
        } else if (orderDTO.getPhone().isBlank()) {
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.ORDER_BAD_PHONE);
        } else if (orderDTO.getOrderItems().isEmpty()) {
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.ORDER_NO_ITEMS);
        } else if (orderDTO.getOrderItems().contains(null)) {
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        orderDTO.getOrderItems().forEach(orderItemDto -> {
            int availableBooks = orderItemDto.getBook().getStock().getAvailable();
            int orderBooks = orderItemDto.getCount();
            if (orderBooks > availableBooks) {
                throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.ORDER_ITEM_NOT_IN_STOCK);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = {ResponseStatusException.class})
    public OrderDTO createOrder(OrderDTO orderDTO, CardInfo cardInfo) throws ResponseStatusException {
        validateOrder(orderDTO);

        //edit local stock
        //create order
        orderDTO.getOrderItems().forEach(orderItemDto -> {
            StockDTO stockDTO = orderItemDto.getBook().getStock();
            stockDTO.setAvailable(stockDTO.getAvailable() - orderItemDto.getCount());
            stockDTO.setInDelivery(stockDTO.getInDelivery() + orderItemDto.getCount());
        });

        Order orderEntity = orderMapper.dtoToEntity(orderDTO);

        orderEntity.getOrderItems().forEach(orderItem -> bookRepository.save(orderItem.getBook()));
        orderRepository.save(orderEntity);

        //send payment request to the external service
        //check payment status
        PaymentsInfoResponse paymentsInfoResponse = null;
        try {
            paymentsInfoResponse = paymentsService.processPayment(orderDTO, cardInfo);
        } catch (PaymentException e) {
            PaymentsErrorResponse errorResponse = e.getErrorResponse();
            log.warn(errorResponse.toString());
            //if failed - rollback
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.PAYMENT_ERROR);
        }
        log.info(paymentsInfoResponse.toString());

        return orderMapper.entityToDto(orderEntity);
    }
}

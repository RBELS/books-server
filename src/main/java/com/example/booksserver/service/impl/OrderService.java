package com.example.booksserver.service.impl;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.ResponseStatusWithBodyExceptionFactory;
import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.dto.StockDTO;
import com.example.booksserver.entity.order.Order;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.external.IPaymentsRequestService;
import com.example.booksserver.external.PaymentException;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.service.IOrderService;
import com.example.booksserver.userstate.CardInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final IPaymentsRequestService paymentsService;
    private final BookRepository bookRepository;
    private final ResponseStatusWithBodyExceptionFactory exceptionFactory;

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
    @Transactional(noRollbackFor = ResponseStatusException.class)
    public OrderDTO createOrder(OrderDTO orderDTO, CardInfo cardInfo) throws ResponseStatusException {
        validateOrder(orderDTO);

        try {
            paymentsService.processPayment(orderDTO, cardInfo);
            orderDTO.setStatus(OrderStatus.SUCCESS);
        } catch (PaymentException e) {
            orderDTO.setStatus(e.getStatus());
        }

        if (!orderDTO.getStatus().equals(OrderStatus.FAIL)) {
            orderDTO.getOrderItems().forEach(orderItemDto -> {
                StockDTO stockDTO = orderItemDto.getBook().getStock();
                stockDTO.setAvailable(stockDTO.getAvailable() - orderItemDto.getCount());
                stockDTO.setInDelivery(stockDTO.getInDelivery() + orderItemDto.getCount());
            });
        }

        Order orderEntity = orderMapper.dtoToEntity(orderDTO);
        orderEntity.getOrderItems().forEach(orderItem -> bookRepository.save(orderItem.getBook()));
        orderRepository.save(orderEntity);


        if (orderDTO.getStatus() == OrderStatus.FAIL) {
            throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.PAYMENT_ERROR);
        }

        return orderMapper.entityToDto(orderEntity);
    }

    @Transactional
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 5)
    public void updateOrderStatuses() {
        List<OrderDTO> orderDTOList = orderMapper.entityToDto(
                orderRepository.findAllByStatus(OrderStatus.PENDING)
        );
        orderDTOList.forEach(orderDTO -> {
            PaymentsInfoResponse response;
            try {
                response = paymentsService.getPaymentInfo(orderDTO.getId());
            } catch (PaymentException e) {
                if (e.getStatus().equals(OrderStatus.FAIL)) {
                    orderDTO.setStatus(OrderStatus.FAIL);
                    orderDTO.getOrderItems().forEach(orderItemDTO -> {
                        int orderCount = orderItemDTO.getCount();
                        StockDTO stockDTO = orderItemDTO.getBook().getStock();
                        stockDTO.setAvailable(stockDTO.getAvailable() + orderCount);
                        stockDTO.setInDelivery(stockDTO.getInDelivery() - orderCount);
                    });

                    Order orderEntity = orderMapper.dtoToEntity(orderDTO);
                    orderEntity.getOrderItems().forEach(orderItem -> bookRepository.save(orderItem.getBook()));
                    orderRepository.save(orderEntity);
                }
                return;
            }

            if (response.getStatus().equals("SUCCESS")) {
                orderDTO.setStatus(OrderStatus.SUCCESS);
                Order orderEntity = orderMapper.dtoToEntity(orderDTO);
                orderRepository.save(orderEntity);
            }
        });
    }
}

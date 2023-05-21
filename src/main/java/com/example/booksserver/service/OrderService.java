package com.example.booksserver.service;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.dto.StockDTO;
import com.example.booksserver.entity.Order;
import com.example.booksserver.entity.OrderItem;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.request.IPaymentsRequestService;
import com.example.booksserver.request.PaymentException;
import com.example.booksserver.request.response.PaymentInfoResponse;
import com.example.booksserver.request.response.PaymentsErrorResponse;
import com.example.booksserver.userstate.CardInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final IPaymentsRequestService mockPaymentsService;
    private final BookRepository bookRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            @Qualifier("mockPaymentsService") IPaymentsRequestService mockPaymentsService,
            BookRepository bookRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.mockPaymentsService = mockPaymentsService;
        this.bookRepository = bookRepository;
    }

    private void validateOrder(OrderDTO orderDTO) throws ResponseStatusException {
        if (
                orderDTO.getEmail().isBlank() || orderDTO.getAddress().isBlank()
                || orderDTO.getName().isBlank() || orderDTO.getPhone().isBlank()
                || orderDTO.getOrderItems().isEmpty()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (orderDTO.getOrderItems().contains(null)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        orderDTO.getOrderItems().forEach(orderItemDto -> {
            int availableBooks = orderItemDto.getBook().getStock().getAvailable();
            int orderBooks = orderItemDto.getCount();
            if (orderBooks > availableBooks) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
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
        PaymentInfoResponse paymentInfoResponse;
        try {
            paymentInfoResponse = mockPaymentsService.processPayment(orderDTO, cardInfo);
        } catch (PaymentException e) {
            PaymentsErrorResponse errorResponse = e.getErrorResponse();
            log.warn(errorResponse.toString());
            //if failed - rollback
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        log.info(paymentInfoResponse.toString());

        return orderMapper.entityToDto(orderEntity);
    }
}

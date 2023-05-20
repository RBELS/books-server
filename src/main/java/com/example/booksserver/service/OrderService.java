package com.example.booksserver.service;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.entity.Order;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.userstate.CardInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    private void validateOder(OrderDTO orderDTO) throws ResponseStatusException {
        if (
                orderDTO.getEmail().isBlank() || orderDTO.getAddress().isBlank()
                || orderDTO.getName().isBlank() || orderDTO.getPhone().isBlank()
                || orderDTO.getOrderItems().isEmpty()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (orderDTO.getOrderItems().contains(null)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // TODO: check if is in stock
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO, CardInfo cardInfo) throws ResponseStatusException {
        validateOder(orderDTO);
        //edit local stock
        //create order
        //send payment request to the external service
        //check payment status
        //if failed - rollback
        return null;
    }
}

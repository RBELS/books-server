package com.example.booksserver.service;

import com.example.booksserver.dto.Order;
import com.example.booksserver.dto.Stock;
import com.example.booksserver.userstate.CardInfo;
import org.springframework.web.server.ResponseStatusException;

public interface IOrderService {
    Order createOrder(Order order, CardInfo cardInfo) throws ResponseStatusException;
    Order cancelOrder(Order order) throws ResponseStatusException;
    Order getOrderById(Long orderId) throws ResponseStatusException;

    static void moveStock(Order order, boolean isReverse) {
        order.getOrderItems().forEach(orderItemDto -> {
            Stock stock = orderItemDto.getBook().getStock();
            int moveModifier = isReverse ? -1 : 1;
            stock.setAvailable(stock.getAvailable() - moveModifier * orderItemDto.getCount());
            stock.setInDelivery(stock.getInDelivery() + moveModifier * orderItemDto.getCount());
        });
    }
}

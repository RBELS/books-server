package com.example.booksserver.service;

import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.service.Stock;
import com.example.booksserver.model.service.CardInfo;
import org.springframework.web.server.ResponseStatusException;

public interface OrderService {
    Order createOrder(Order order, CardInfo cardInfo) throws ResponseStatusException;
    Order cancelOrder(Long orderId) throws ResponseStatusException;
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

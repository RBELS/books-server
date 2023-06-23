package com.example.booksserver.controller;

import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.service.OrderItem;
import com.example.booksserver.service.ContentService;
import com.example.booksserver.service.OrderService;
import com.example.booksserver.model.dto.request.PostOrdersRequest;
import com.example.booksserver.model.dto.response.CancelOrderResponse;
import com.example.booksserver.model.dto.response.PostOrdersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final ContentService contentService;
    private final OrderService orderService;

    @PostMapping(value = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PostOrdersResponse order(
            @RequestBody PostOrdersRequest request
    ) {
        // create an order dto. Status is not set here
        Order order = Order.builder()
                .email(request.getInfo().getEmail())
                .phone(request.getInfo().getPhone())
                .name(request.getInfo().getName())
                .address(request.getInfo().getAddress())
                .build();

        // create order items
        List<OrderItem> orderItemList = request.getInfo().getBooks().stream().map(ordersBook -> {
            Book book = contentService.getBookById(ordersBook.getId());
            return OrderItem.builder()
                    .book(book)
                    .count(ordersBook.getCount())
                    .price(book.getPrice())
                    .build();
        }).toList();
        order.getOrderItems().addAll(orderItemList);

        Order resultDTO = orderService.createOrder(order, request.getCard());

        return new PostOrdersResponse(resultDTO, "SUCCESS");
    }

    @PatchMapping(value = "/orders/{orderId}/cancel")
    public CancelOrderResponse cancelOrder(
            @PathVariable Long orderId
    ) {
        Order order = orderService.getOrderById(orderId);
        order = orderService.cancelOrder(order);
        return new CancelOrderResponse()
                .setOrderNo(String.valueOf(order.getId()))
                .setStatus("CANCELED");
    }
}

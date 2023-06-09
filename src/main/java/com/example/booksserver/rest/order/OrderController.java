package com.example.booksserver.rest.order;

import com.example.booksserver.dto.Book;
import com.example.booksserver.dto.Order;
import com.example.booksserver.dto.OrderItem;
import com.example.booksserver.service.IContentService;
import com.example.booksserver.service.IOrderService;
import com.example.booksserver.userstate.request.PostOrdersRequest;
import com.example.booksserver.userstate.response.CancelOrderResponse;
import com.example.booksserver.userstate.response.PostOrdersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final IContentService contentService;
    private final IOrderService orderService;

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
        Order order = orderService.cancelOrder(orderId);
        return new CancelOrderResponse()
                .setOrderNo(String.valueOf(order.getId()))
                .setStatus("CANCELED");
    }
}

package com.example.booksserver.rest.order;

import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.dto.OrderItemDTO;
import com.example.booksserver.service.IContentService;
import com.example.booksserver.service.impl.OrderService;
import com.example.booksserver.userstate.request.PostOrdersRequest;
import com.example.booksserver.userstate.response.PostOrdersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final IContentService contentService;
    private final OrderService orderService;

    @PostMapping(value = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PostOrdersResponse order(
            @RequestBody PostOrdersRequest request
    ) {
        // create an order dto. Status is not set here
        OrderDTO orderDTO = OrderDTO.builder()
                .email(request.getInfo().getEmail())
                .phone(request.getInfo().getPhone())
                .name(request.getInfo().getName())
                .address(request.getInfo().getAddress())
                .build();

        // create order items
        List<OrderItemDTO> orderItemDTOList = request.getInfo().getBooks().stream().map(ordersBook -> {
            BookDTO bookDTO = contentService.getBookById(ordersBook.getId());
            return OrderItemDTO.builder()
                    .book(bookDTO)
                    .count(ordersBook.getCount())
                    .price(bookDTO.getPrice())
                    .build();
        }).toList();
        orderDTO.getOrderItems().addAll(orderItemDTOList);

        OrderDTO resultDTO = orderService.createOrder(orderDTO, request.getCard());

        return new PostOrdersResponse(resultDTO);
    }
}

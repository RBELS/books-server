package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.Order;
import com.example.booksserver.userstate.request.PostOrdersRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PostOrdersResponse {
    private String status;
    private String orderNo;
    private List<PostOrdersRequest.OrdersBook> books;

    public PostOrdersResponse(Order order, String status) {
        this.status = status;
        this.orderNo = String.valueOf(order.getId());
        this.books = order.getOrderItems().stream().map(PostOrdersRequest.OrdersBook::new).toList();
    }
}

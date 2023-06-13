package com.example.booksserver.model.dto.response;

import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.dto.request.PostOrdersRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PostOrdersResponse {
    private String status;
    private String orderNo;
    private List<PostOrdersRequest.OrdersBookDTO> books;

    public PostOrdersResponse(Order order, String status) {
        this.status = status;
        this.orderNo = String.valueOf(order.getId());
        this.books = order.getOrderItems().stream().map(PostOrdersRequest.OrdersBookDTO::new).toList();
    }
}

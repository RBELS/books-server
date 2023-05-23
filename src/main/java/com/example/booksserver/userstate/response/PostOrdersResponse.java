package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.userstate.request.PostOrdersRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PostOrdersResponse {
    private String status;
    private String orderNo;
    private List<PostOrdersRequest.OrdersBook> books;

    public PostOrdersResponse(OrderDTO orderDTO) {
        this.status = "SUCCESS";
        this.orderNo = String.valueOf(orderDTO.getId());
        this.books = orderDTO.getOrderItems().stream().map(PostOrdersRequest.OrdersBook::new).toList();
    }
}

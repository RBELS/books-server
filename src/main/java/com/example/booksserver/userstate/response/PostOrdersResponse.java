package com.example.booksserver.userstate.response;

import com.example.booksserver.userstate.request.PostOrdersRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostOrdersResponse {
    private String status;
    private String orderNo;
    private List<PostOrdersRequest.OrdersBook> books;
}

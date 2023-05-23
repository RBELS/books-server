package com.example.booksserver.userstate.request;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.dto.OrderItemDTO;
import com.example.booksserver.userstate.CardInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PostOrdersRequest {

    @NoArgsConstructor
    @Data
    public static class OrdersBook {
        private Long id;
        private Integer count;

        public OrdersBook(OrderItemDTO orderItemDTO) {
            this.id = orderItemDTO.getBook().getId();
            this.count = orderItemDTO.getCount();
        }
    }

    @NoArgsConstructor
    @Data
    public static class OrderInfo {
        private String address;
        private String email;
        private String phone;
        private String name;
        private List<OrdersBook> books;
    }

    private CardInfo card;
    private OrderInfo info;
}

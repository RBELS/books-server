package com.example.booksserver.userstate.request;

import com.example.booksserver.dto.OrderItem;
import com.example.booksserver.userstate.CardInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class PostOrdersRequest {

    @NoArgsConstructor
    @Data
    @Accessors(chain = true)
    public static class OrdersBook {
        private Long id;
        private Integer count;

        public OrdersBook(OrderItem orderItem) {
            this.id = orderItem.getBook().getId();
            this.count = orderItem.getCount();
        }
    }

    @NoArgsConstructor
    @Data
    @Accessors(chain = true)
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

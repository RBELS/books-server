package com.example.booksserver.model.dto.request;

import com.example.booksserver.model.service.OrderItem;
import com.example.booksserver.model.service.CardInfo;
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
    public static class OrdersBookDTO {
        private Long id;
        private Integer count;

        public OrdersBookDTO(OrderItem orderItem) {
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
        private List<OrdersBookDTO> books;
    }

    private CardInfo card;
    private OrderInfo info;
}

package com.example.booksserver.external.request;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.userstate.CardInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class PostPaymentsRequest {
    private CardInfo card;
    private String externalId;
    private BigDecimal sum;

    public PostPaymentsRequest(OrderDTO orderDTO, CardInfo cardInfo) {
        this.card = cardInfo;
        this.externalId = String.valueOf(orderDTO.getId());
        AtomicLong totalSum = new AtomicLong(0L);
        orderDTO.getOrderItems().forEach(orderItemDTO -> totalSum.addAndGet(orderItemDTO.getPrice()*orderItemDTO.getCount()));
        this.sum = new BigDecimal(String.format("%.2f", totalSum.get() / 100.0));
    }
}

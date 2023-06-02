package com.example.booksserver.external.request;

import com.example.booksserver.dto.Order;
import com.example.booksserver.userstate.CardInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class PostPaymentsRequest {
    private CardInfo card;
    private String externalId;
    private BigDecimal sum;

    public PostPaymentsRequest(Order order, CardInfo cardInfo) {
        this.card = cardInfo;
        this.externalId = String.valueOf(order.getId());
        AtomicReference<BigDecimal> totalSum = new AtomicReference<>(BigDecimal.ZERO);
        order.getOrderItems().forEach(orderItemDTO -> {
            BigDecimal bufPrice = orderItemDTO.getPrice().multiply(new BigDecimal(orderItemDTO.getCount()));
            totalSum.set(totalSum.get().add(bufPrice));
        });
        this.sum = totalSum.get();
    }
}

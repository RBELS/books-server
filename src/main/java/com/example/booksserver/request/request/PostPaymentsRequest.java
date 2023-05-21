package com.example.booksserver.request.request;

import com.example.booksserver.userstate.CardInfo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostPaymentsRequest {
    private CardInfo card;
    private String externalId;
    private BigDecimal sum;
}

package com.example.booksserver.request.response;

import com.example.booksserver.userstate.CardInfo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentInfoResponse {
    private String id;
    private String status;
    private CardInfo card;
    private String externalId;
    private BigDecimal sum;
}
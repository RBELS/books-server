package com.example.booksserver.model.dto.response;

import com.example.booksserver.model.service.CardInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class PaymentsInfoResponse {
    private String id;
    private String status;
    private CardInfo card;
    private String externalId;
    private BigDecimal sum;
}

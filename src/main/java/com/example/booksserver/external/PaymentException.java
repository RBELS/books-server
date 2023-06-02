package com.example.booksserver.external;

import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.external.response.PaymentsErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentException extends Exception {
    private final OrderStatus status;
    private final PaymentsErrorResponse errorResponse;
}

package com.example.booksserver.external;

import com.example.booksserver.external.response.PaymentsErrorResponse;
import lombok.Getter;

public class FailPaymentException extends PaymentException {
    @Getter
    private final PaymentsErrorResponse errorResponse;
    public FailPaymentException(PaymentsErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}

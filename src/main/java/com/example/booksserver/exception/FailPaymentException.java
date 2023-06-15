package com.example.booksserver.exception;

import com.example.booksserver.model.dto.response.PaymentsErrorResponse;
import lombok.Getter;

public class FailPaymentException extends PaymentException {
    @Getter
    private final PaymentsErrorResponse errorResponse;
    public FailPaymentException(PaymentsErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}

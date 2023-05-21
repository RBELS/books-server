package com.example.booksserver.request;

import com.example.booksserver.request.response.PaymentsErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentException extends Exception {
    private final PaymentsErrorResponse errorResponse;
}

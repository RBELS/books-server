package com.example.booksserver.rest;

import com.example.booksserver.exception.FailPaymentException;
import com.example.booksserver.exception.UnreachablePaymentException;
import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.dto.response.PaymentsInfoResponse;
import com.example.booksserver.model.service.CardInfo;

public interface PaymentClient {
    PaymentsInfoResponse processPayment(Order order, CardInfo cardInfo) throws FailPaymentException, UnreachablePaymentException;
    PaymentsInfoResponse getPaymentInfo(long orderId) throws FailPaymentException, UnreachablePaymentException;
    PaymentsInfoResponse cancelPayment(long orderId) throws FailPaymentException, UnreachablePaymentException;
}

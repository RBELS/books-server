package com.example.booksserver.external;

import com.example.booksserver.dto.Order;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.userstate.CardInfo;

public interface IPaymentsRequestService {
    PaymentsInfoResponse processPayment(Order order, CardInfo cardInfo) throws FailPaymentException, UnreachablePaymentException;
    PaymentsInfoResponse getPaymentInfo(long orderId) throws FailPaymentException, UnreachablePaymentException;
    PaymentsInfoResponse cancelPayment(long orderId) throws FailPaymentException, UnreachablePaymentException;
}

package com.example.booksserver.external;

import com.example.booksserver.dto.Order;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.userstate.CardInfo;

public interface IPaymentsRequestService {
    PaymentsInfoResponse processPayment(Order order, CardInfo cardInfo) throws PaymentException;
    PaymentsInfoResponse getPaymentInfo(long orderId) throws PaymentException;
    PaymentsInfoResponse cancelPayment(long orderId) throws PaymentException;
}

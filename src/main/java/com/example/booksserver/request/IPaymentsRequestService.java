package com.example.booksserver.request;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.request.response.PaymentInfoResponse;
import com.example.booksserver.userstate.CardInfo;

public interface IPaymentsRequestService {
    PaymentInfoResponse processPayment(OrderDTO orderDTO, CardInfo cardInfo) throws PaymentException;
    PaymentInfoResponse getPaymentInfo(long orderId) throws PaymentException;
    PaymentInfoResponse cancelPayment(long orderId) throws PaymentException;
}

package com.example.booksserver.external;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.userstate.CardInfo;

public interface IPaymentsRequestService {
    PaymentsInfoResponse processPayment(OrderDTO orderDTO, CardInfo cardInfo) throws PaymentException;
    PaymentsInfoResponse getPaymentInfo(long orderId) throws PaymentException;
    PaymentsInfoResponse cancelPayment(long orderId) throws PaymentException;
}

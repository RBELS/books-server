package com.example.booksserver.request;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.request.response.PaymentInfoResponse;
import com.example.booksserver.userstate.CardInfo;
import org.springframework.stereotype.Service;

@Service("paymentsService")
public class PaymentsRequestService implements IPaymentsRequestService {
    @Override
    public PaymentInfoResponse processPayment(OrderDTO orderDTO, CardInfo cardInfo) throws PaymentException {
        return null;
    }

    @Override
    public PaymentInfoResponse getPaymentInfo(long orderId) throws PaymentException {
        return null;
    }

    @Override
    public PaymentInfoResponse cancelPayment(long orderId) throws PaymentException {
        return null;
    }
}

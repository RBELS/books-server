package com.example.booksserver.request;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.request.response.PaymentInfoResponse;
import com.example.booksserver.request.response.PaymentsErrorResponse;
import com.example.booksserver.userstate.CardInfo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

@Service("mockPaymentsService")
public class MockPaymentsService implements IPaymentsRequestService {
    @Override
    public PaymentInfoResponse processPayment(OrderDTO orderDTO, CardInfo cardInfo) throws PaymentException {
        PaymentInfoResponse resultResponse = new PaymentInfoResponse();
        resultResponse.setId(String.valueOf(0L));

        AtomicLong totalSum = new AtomicLong(0L);
        orderDTO.getOrderItems().forEach(orderItemDto -> totalSum.addAndGet(orderItemDto.getPrice()));

        resultResponse.setSum(new BigDecimal(String.format("%.2f", totalSum.get() / 100.0)));
        resultResponse.setStatus("SUCCESS");
        resultResponse.setCard(cardInfo);
        resultResponse.setExternalId(String.valueOf(orderDTO.getId()));
        return resultResponse;
    }

    @Override
    public PaymentInfoResponse getPaymentInfo(long orderId) throws PaymentException {
        return mockPaymentInfoResponse(orderId);
    }

    @Override
    public PaymentInfoResponse cancelPayment(long orderId) throws PaymentException {
        return mockPaymentInfoResponse(orderId);
    }

    private PaymentInfoResponse mockPaymentInfoResponse(long orderId) {
        PaymentInfoResponse resultResponse = new PaymentInfoResponse();
        resultResponse.setId(String.valueOf(0L));

        resultResponse.setSum(new BigDecimal(String.format("%.2f", 1000L / 100.0)));
        resultResponse.setStatus("SUCCESS");

        CardInfo cardInfo = new CardInfo();
        cardInfo.setCvv("100");
        cardInfo.setName("Holder Name");
        cardInfo.setNumber("1111222233334444");

        resultResponse.setCard(cardInfo);
        resultResponse.setExternalId(String.valueOf(orderId));
        return resultResponse;
    }
}

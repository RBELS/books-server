package com.example.booksserver.external.impl;

import com.example.booksserver.dto.Order;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.external.IPaymentsRequestService;
import com.example.booksserver.external.PaymentException;
import com.example.booksserver.external.request.PostPaymentsRequest;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.external.response.PaymentsErrorResponse;
import com.example.booksserver.userstate.CardInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.util.Objects;

@Slf4j
@Service
public class PaymentsRequestService implements IPaymentsRequestService {
    @Value("${external.payment-service.host}")
    private String paymentServiceAddress;
    @Value("${external.payment-service.post-payment-mapping}")
    private String paymentsMapping;

    @Override
    public PaymentsInfoResponse processPayment(Order order, CardInfo cardInfo) throws PaymentException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PaymentsInfoResponse> infoResponse;
        String requestUrl = paymentServiceAddress + paymentsMapping;
        try {
            infoResponse = restTemplate.postForEntity(requestUrl, new PostPaymentsRequest(order, cardInfo), PaymentsInfoResponse.class);
        } catch (RestClientResponseException e) {
            PaymentsErrorResponse errorResponse = e.getResponseBodyAs(PaymentsErrorResponse.class);
            if (!Objects.isNull(errorResponse)) {
                log.info(errorResponse.toString());
                throw new PaymentException(OrderStatus.FAIL, errorResponse);
            } else {
                log.error(requestUrl + " endpoint returned an unknown error response body type.");
                throw new PaymentException(OrderStatus.FAIL, null);
            }
        } catch (RestClientException e) {
            throw new PaymentException(OrderStatus.PENDING, null);
        }

        if (Objects.isNull(infoResponse.getBody())) {
            log.error(requestUrl + " endpoint returned an unknown response body type.");
            throw new PaymentException(OrderStatus.FAIL, null);
        }

        return infoResponse.getBody();
    }

    @Override
    public PaymentsInfoResponse getPaymentInfo(long orderId) throws PaymentException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PaymentsInfoResponse> infoResponse;
        String requestUrl = String.format("%s%s/%d", paymentServiceAddress, paymentsMapping, orderId);
        try {
            infoResponse = restTemplate.getForEntity(requestUrl, PaymentsInfoResponse.class);
        } catch (RestClientResponseException e) {
            PaymentsErrorResponse errorResponse = e.getResponseBodyAs(PaymentsErrorResponse.class);
            throw new PaymentException(OrderStatus.FAIL, errorResponse);
        } catch (RestClientException e) {
            throw new PaymentException(OrderStatus.PENDING, null);
        }

        return infoResponse.getBody();
    }

    @Override
    public PaymentsInfoResponse cancelPayment(long orderId) throws PaymentException {
        RestTemplate restTemplate = new RestTemplate();
        PaymentsInfoResponse infoResponse = null;
        String requestUrl = String.format("%s%s/%d/cancel", paymentServiceAddress, paymentsMapping, orderId);
        try {
            infoResponse = restTemplate.patchForObject(requestUrl, null, PaymentsInfoResponse.class);
        } catch (RestClientResponseException e) {
            throw new PaymentException(OrderStatus.UNKNOWN, e.getResponseBodyAs(PaymentsErrorResponse.class));
        } catch (RestClientException e) {
            throw new PaymentException(OrderStatus.PENDING_CANCEL, null);
        }
        return infoResponse;
    }
}

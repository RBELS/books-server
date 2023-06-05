package com.example.booksserver.external.impl;

import com.example.booksserver.dto.Order;
import com.example.booksserver.external.FailPaymentException;
import com.example.booksserver.external.IPaymentService;
import com.example.booksserver.external.UnreachablePaymentException;
import com.example.booksserver.external.request.PostPaymentsRequest;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.external.response.PaymentsErrorResponse;
import com.example.booksserver.userstate.CardInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {
    @Value("${external.payment-service.host}")
    private String paymentServiceAddress;
    @Value("${external.payment-service.post-payment-mapping}")
    private String paymentsMapping;
    private final RestTemplate restTemplate;

    @Override
    public PaymentsInfoResponse processPayment(Order order, CardInfo cardInfo) throws FailPaymentException, UnreachablePaymentException {
        ResponseEntity<PaymentsInfoResponse> infoResponse;
        String requestUrl = paymentServiceAddress + paymentsMapping;

        try {
            infoResponse = restTemplate.postForEntity(requestUrl, new PostPaymentsRequest(order, cardInfo), PaymentsInfoResponse.class);
        } catch (RestClientResponseException e) {
            PaymentsErrorResponse errorResponse = e.getResponseBodyAs(PaymentsErrorResponse.class);
            log.info(errorResponse.toString());
            throw new FailPaymentException(errorResponse);
        } catch (RestClientException e) {
            throw new UnreachablePaymentException();
        }

        return infoResponse.getBody();
    }

    @Override
    public PaymentsInfoResponse getPaymentInfo(long orderId) throws FailPaymentException, UnreachablePaymentException {
        ResponseEntity<PaymentsInfoResponse> infoResponse;
        String requestUrl = String.format("%s%s/%d", paymentServiceAddress, paymentsMapping, orderId);

        try {
            infoResponse = restTemplate.getForEntity(requestUrl, PaymentsInfoResponse.class);
        } catch (RestClientResponseException e) {
            PaymentsErrorResponse errorResponse = e.getResponseBodyAs(PaymentsErrorResponse.class);
            throw new FailPaymentException(errorResponse);
        } catch (RestClientException e) {
            throw new UnreachablePaymentException();
        }

        return infoResponse.getBody();
    }

    @Override
    public PaymentsInfoResponse cancelPayment(long orderId) throws FailPaymentException, UnreachablePaymentException {
        PaymentsInfoResponse infoResponse;
        String requestUrl = String.format("%s%s/%d/cancel", paymentServiceAddress, paymentsMapping, orderId);
        try {
            infoResponse = restTemplate.patchForObject(requestUrl, null, PaymentsInfoResponse.class);
        } catch (RestClientResponseException e) {
            throw new FailPaymentException(e.getResponseBodyAs(PaymentsErrorResponse.class));
        } catch (RestClientException e) {
            throw new UnreachablePaymentException();
        }
        return infoResponse;
    }
}

package com.example.booksserver.external;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.external.request.PostPaymentsRequest;
import com.example.booksserver.external.response.PaymentsInfoResponse;
import com.example.booksserver.external.response.PaymentsErrorResponse;
import com.example.booksserver.userstate.CardInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Service("paymentsService")
public class PaymentsRequestService implements IPaymentsRequestService {
    @Value("${external.payment-service.host}")
    private String paymentServiceAddress;
    @Value("${external.payment-service.post-payment-mapping}")
    private String postPaymentsMapping;

    @Override
    public PaymentsInfoResponse processPayment(OrderDTO orderDTO, CardInfo cardInfo) throws PaymentException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PaymentsInfoResponse> infoResponse;
        String requestUrl = paymentServiceAddress + postPaymentsMapping;
        try {
            infoResponse = restTemplate.postForEntity(requestUrl, new PostPaymentsRequest(orderDTO, cardInfo), PaymentsInfoResponse.class);
            // TODO: Use RestClientException and RestServerException
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            PaymentsErrorResponse errorResponse = e.getResponseBodyAs(PaymentsErrorResponse.class);
            if (!Objects.isNull(errorResponse)) {
                log.info(errorResponse.toString());
                throw new PaymentException(errorResponse);
            } else {
                log.error(requestUrl + " endpoint returned an unknown error response body type.");
                throw new PaymentException(null);
            }
        }

        if (Objects.isNull(infoResponse.getBody())) {
            log.error(requestUrl + " endpoint returned an unknown response body type.");
            throw new PaymentException(null);
        }

        return infoResponse.getBody();
    }

    @Override
    public PaymentsInfoResponse getPaymentInfo(long orderId) throws PaymentException {
        return null;
    }

    @Override
    public PaymentsInfoResponse cancelPayment(long orderId) throws PaymentException {
        return null;
    }
}

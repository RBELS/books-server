package com.example.booksserver.integration;

import com.example.booksserver.exception.FailPaymentException;
import com.example.booksserver.exception.InternalErrorCode;
import com.example.booksserver.exception.UnreachablePaymentException;
import com.example.booksserver.model.dto.response.*;
import com.example.booksserver.model.entity.OrderEntity;
import com.example.booksserver.model.entity.OrderStatus;
import com.example.booksserver.model.service.CardInfo;
import com.example.booksserver.model.dto.request.PostOrdersRequest;
import com.example.booksserver.model.service.Order;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.rest.PaymentClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderTest {
    @MockBean
    private JwtDecoder jwtDecoder;
    @MockBean
    private PaymentClient paymentClient;

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrderRepository orderRepository;

    private PostOrdersRequest postOrdersRequest;
    {
        postOrdersRequest = new PostOrdersRequest()
                .setCard(new CardInfo()
                        .setCvv("101")
                        .setName("Holder Name")
                        .setNumber("1111222233334444")
                )
                .setInfo(new PostOrdersRequest.OrderInfo()
                        .setName("Name")
                        .setAddress("Address")
                        .setEmail("Email")
                        .setPhone("Phone")
                );
    }
    private static final PaymentsInfoResponse successResponse;
    private static final PaymentsErrorResponse errorResponse;
    private static final PaymentsInfoResponse cancelResponse;

    static {
        successResponse = new PaymentsInfoResponse()
                .setId("1")
                .setExternalId("101")
                .setStatus("SUCCESS")
                .setSum(new BigDecimal("19.99"))
                .setCard(mock(CardInfo.class));
        errorResponse = new PaymentsErrorResponse()
                .setCode("202")
                .setInternalCode("303")
                .setDevMessage("Dev message");
        errorResponse
                .setUserMessage(new PaymentsErrorResponse.UserMessage()
                        .setLangEn("Eng message")
                        .setLangRu("Сообщение на русском")
                );
        cancelResponse = new PaymentsInfoResponse()
                .setId("1")
                .setExternalId("101")
                .setStatus("CANCELED")
                .setSum(new BigDecimal("19.99"))
                .setCard(mock(CardInfo.class));
    }

    private PostOrdersRequest.OrdersBookDTO ordersBookDTO;

    @BeforeEach
    @WithMockUser
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        long authorId = AuthorTest.createAuthor(mockMvc, objectMapper, "Author Name")
                .getAuthor().getId();
        PostBooksResponse postBooksResponse = BookTest.createBook(mockMvc, objectMapper,
                BookTest.defaultRequest
                        .setAuthorIdList(Arrays.asList(authorId))
        );

        ordersBookDTO = new PostOrdersRequest.OrdersBookDTO()
                .setId(postBooksResponse.getBook().getId())
                .setCount(3);
    }

    public static PostOrdersResponse createOrderForOk(
            PostOrdersRequest postOrdersRequest,
            PostOrdersRequest.OrdersBookDTO ordersBookDTO,
            MockMvc mockMvc,
            ObjectMapper objectMapper
    ) throws Exception {
        postOrdersRequest.getInfo().setBooks(Arrays.asList(ordersBookDTO));
        String postOrdersResponseStr = mockMvc
                .perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postOrdersRequest))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(postOrdersResponseStr, PostOrdersResponse.class);
    }

    @Test
    public void postOrderWithSuccess() throws Exception {
        when(paymentClient.processPayment(any(Order.class), any(CardInfo.class)))
                .thenReturn(successResponse);
        when(paymentClient.getPaymentInfo(anyLong()))
                .thenReturn(successResponse);

        PostOrdersResponse postOrdersResponse = createOrderForOk(postOrdersRequest, ordersBookDTO, mockMvc, objectMapper);

        assertThat(postOrdersResponse.getBooks().get(0).getCount()).isEqualTo(ordersBookDTO.getCount());
        assertThat(postOrdersResponse.getBooks().get(0).getId()).isEqualTo(ordersBookDTO.getId());

        assertThat(postOrdersResponse.getStatus()).isEqualTo("SUCCESS");

        long orderId = Long.parseLong(postOrdersResponse.getOrderNo());
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow();
        assertThat(orderEntity.getStatus()).isEqualTo(OrderStatus.SUCCESS);
    }

    public static ErrorResponse createOrderForError(
            PostOrdersRequest postOrdersRequest,
            PostOrdersRequest.OrdersBookDTO ordersBookDTO,
            MockMvc mockMvc,
            ObjectMapper objectMapper
    ) throws Exception {
        postOrdersRequest.getInfo().setBooks(Arrays.asList(ordersBookDTO));
        String postOrdersResponseStr = mockMvc
                .perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postOrdersRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(postOrdersResponseStr, ErrorResponse.class);
    }

    @Test
    public void postOrderWithFail() throws Exception {
        when(paymentClient.processPayment(any(Order.class), any(CardInfo.class)))
                .thenThrow(new FailPaymentException(errorResponse));
        when(paymentClient.getPaymentInfo(anyLong()))
                .thenThrow(new FailPaymentException(errorResponse));

        ErrorResponse postOrdersResponse = createOrderForError(postOrdersRequest, ordersBookDTO, mockMvc, objectMapper);
        assertThat(postOrdersResponse.getInternalCode()).isEqualTo(String.valueOf(InternalErrorCode.PAYMENT_ERROR.getValue()));

        OrderEntity orderEntity = orderRepository.findAll().get(0);
        assertThat(orderEntity.getStatus()).isEqualTo(OrderStatus.FAIL);
    }

    @Test
    public void postOrderWithUnreached() throws Exception {
        when(paymentClient.processPayment(any(Order.class), any(CardInfo.class)))
                .thenThrow(new UnreachablePaymentException());
        when(paymentClient.getPaymentInfo(anyLong()))
                .thenThrow(new UnreachablePaymentException());

        PostOrdersResponse postOrdersResponse = createOrderForOk(postOrdersRequest, ordersBookDTO, mockMvc, objectMapper);

        assertThat(postOrdersResponse.getBooks().get(0).getCount()).isEqualTo(ordersBookDTO.getCount());
        assertThat(postOrdersResponse.getBooks().get(0).getId()).isEqualTo(ordersBookDTO.getId());

        assertThat(postOrdersResponse.getStatus()).isEqualTo("PENDING");

        long orderId = Long.parseLong(postOrdersResponse.getOrderNo());
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow();
        assertThat(orderEntity.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    public void cancelSuccessOrder() throws Exception {
        when(paymentClient.processPayment(any(Order.class), any(CardInfo.class)))
                .thenReturn(successResponse);
        when(paymentClient.getPaymentInfo(anyLong()))
                .thenReturn(successResponse);

        when(paymentClient.cancelPayment(anyLong()))
                .thenReturn(cancelResponse);

        PostOrdersResponse postOrdersResponse = createOrderForOk(postOrdersRequest, ordersBookDTO, mockMvc, objectMapper);

        String cancelOrderResponseStr = mockMvc
                .perform(patch(String.format("/orders/%s/cancel", postOrdersResponse.getOrderNo())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CancelOrderResponse cancelOrderResponse = objectMapper.readValue(cancelOrderResponseStr, CancelOrderResponse.class);

        assertThat(cancelOrderResponse.getOrderNo()).isEqualTo(postOrdersResponse.getOrderNo());
        assertThat(cancelOrderResponse.getStatus()).isEqualTo("CANCELED");

        long orderId = Long.parseLong(postOrdersResponse.getOrderNo());
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow();
        assertThat(orderEntity.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    public void cancelPendingOrderPendingCancel() throws Exception {
        when(paymentClient.processPayment(any(Order.class), any(CardInfo.class)))
                .thenThrow(new UnreachablePaymentException());
        when(paymentClient.getPaymentInfo(anyLong()))
                .thenThrow(new UnreachablePaymentException());

        when(paymentClient.cancelPayment(anyLong()))
                .thenThrow(new UnreachablePaymentException());

        PostOrdersResponse postOrdersResponse = createOrderForOk(postOrdersRequest, ordersBookDTO, mockMvc, objectMapper);

        String cancelOrderResponseStr = mockMvc
                .perform(patch(String.format("/orders/%s/cancel", postOrdersResponse.getOrderNo())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CancelOrderResponse cancelOrderResponse = objectMapper.readValue(cancelOrderResponseStr, CancelOrderResponse.class);
        assertThat(cancelOrderResponse.getOrderNo()).isEqualTo(postOrdersResponse.getOrderNo());
        assertThat(cancelOrderResponse.getStatus()).isEqualTo(OrderStatus.PENDING_CANCEL.name());

        long orderId = Long.parseLong(postOrdersResponse.getOrderNo());
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow();
        assertThat(orderEntity.getStatus()).isEqualTo(OrderStatus.PENDING_CANCEL);
    }

    @Test
    public void cancelPendingOrderErrorCancel() throws Exception {
        when(paymentClient.processPayment(any(Order.class), any(CardInfo.class)))
                .thenThrow(new UnreachablePaymentException());
        when(paymentClient.getPaymentInfo(anyLong()))
                .thenThrow(new UnreachablePaymentException());

        when(paymentClient.cancelPayment(anyLong()))
                .thenThrow(new FailPaymentException(errorResponse));

        PostOrdersResponse postOrdersResponse = createOrderForOk(postOrdersRequest, ordersBookDTO, mockMvc, objectMapper);

        String cancelOrderResponseStr = mockMvc
                .perform(patch(String.format("/orders/%s/cancel", postOrdersResponse.getOrderNo())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CancelOrderResponse cancelOrderResponse = objectMapper.readValue(cancelOrderResponseStr, CancelOrderResponse.class);
        assertThat(cancelOrderResponse.getOrderNo()).isEqualTo(postOrdersResponse.getOrderNo());
        assertThat(cancelOrderResponse.getStatus()).isEqualTo(OrderStatus.CANCELED.name());

        long orderId = Long.parseLong(postOrdersResponse.getOrderNo());
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow();
        assertThat(orderEntity.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

}

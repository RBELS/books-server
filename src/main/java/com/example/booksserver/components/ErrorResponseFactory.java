package com.example.booksserver.components;

import com.example.booksserver.userstate.response.ErrorResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ErrorResponseFactory {
    @Value("#{${responses.error.devMessages}}")
    public Map<InternalErrorCode, String> devMessages;
    @Value("#{${responses.error.langEnMessages}}")
    public Map<InternalErrorCode, String> langEnMessages;
    @Value("#{${responses.error.langRuMessages}}")
    public Map<InternalErrorCode, String> langRuMessages;

    public enum InternalErrorCode {
        BOOK_BAD_AUTHORS(101), BOOK_BAD_NAME(102), BOOK_BAD_RELEASE_YEAR(103), BOOK_BAD_PRICE(104),
        BOOK_BAD_IMAGES(105),

        AUTHOR_BAD_NAME(201),

        ORDER_BAD_EMAIL(301), ORDER_BAD_ADDRESS(302), ORDER_BAD_NAME(303), ORDER_BAD_PHONE(304),
        ORDER_NO_ITEMS(305), ORDER_ITEM_NOT_FOUND(306), ORDER_ITEM_NOT_IN_STOCK(307),

        PAYMENT_ERROR(401),

        IMAGE_NOT_FOUND(501),

        INTERNAL_ERROR_IMAGES(901);

        @Getter
        private final int value;
        public String getStringValue() {
            return String.valueOf(value);
        }

        InternalErrorCode (int value) {
            this.value = value;
        }
    }


    public ErrorResponse create(HttpStatus httpStatus, InternalErrorCode internalErrorCode) {
        return new ErrorResponse(
                httpStatus, internalErrorCode,
                devMessages.get(internalErrorCode), langEnMessages.get(internalErrorCode),
                langRuMessages.get(internalErrorCode)
        );
    }
}

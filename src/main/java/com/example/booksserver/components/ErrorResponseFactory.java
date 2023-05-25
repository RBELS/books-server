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


    public ErrorResponse createObject(HttpStatus httpStatus, InternalErrorCode internalErrorCode) {
        return new ErrorResponse(
                httpStatus, internalErrorCode,
                devMessages.get(internalErrorCode), langEnMessages.get(internalErrorCode),
                langRuMessages.get(internalErrorCode)
        );
    }
}

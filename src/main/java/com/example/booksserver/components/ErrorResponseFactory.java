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


    public ErrorResponse create(HttpStatus httpStatus, InternalErrorCode internalErrorCode) {
        return new ErrorResponse(
                httpStatus, internalErrorCode,
                devMessages.get(internalErrorCode), langEnMessages.get(internalErrorCode),
                langRuMessages.get(internalErrorCode)
        );
    }
}

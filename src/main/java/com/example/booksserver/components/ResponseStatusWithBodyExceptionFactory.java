package com.example.booksserver.components;

import com.example.booksserver.config.ResponseBodyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;

@Component
public class ResponseStatusWithBodyExceptionFactory {
    private final ErrorResponseFactory errorResponseFactory;

    public ResponseStatusWithBodyExceptionFactory(ErrorResponseFactory errorResponseFactory) {
        this.errorResponseFactory = errorResponseFactory;
    }

    public ErrorResponseException create(HttpStatus status, ErrorResponseFactory.InternalErrorCode errorCode) {
        return new ResponseBodyException(
                status,
                errorResponseFactory.create(status, errorCode)
        );
    }
}

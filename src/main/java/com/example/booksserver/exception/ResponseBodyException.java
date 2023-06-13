package com.example.booksserver.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseBodyException extends ResponseStatusException {
    @Getter
    private final Object responseBody;
    public ResponseBodyException(HttpStatus status, Object responseBody) {
        super(status);
        this.responseBody = responseBody;
    }
}

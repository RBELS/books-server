package com.example.booksserver.config;

import com.example.booksserver.exception.ResponseBodyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ResponseBodyExceptionInterceptor extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = { ResponseBodyException.class })
    private ResponseEntity<Object> handleResponseBodyException(ResponseBodyException e, WebRequest request) {
        return handleExceptionInternal(
                e, e.getResponseBody(),
                new HttpHeaders(), e.getStatusCode(),
                request
        );
    }
}

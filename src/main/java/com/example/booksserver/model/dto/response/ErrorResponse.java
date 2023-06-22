package com.example.booksserver.model.dto.response;

import com.example.booksserver.exception.InternalErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class ErrorResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserMessage {
        @JsonProperty("lang_en")
        private String langEn;
        @JsonProperty("lang_ru")
        private String langRu;
    }

    private String code;
    private String internalCode;
    private String devMessage;
    private UserMessage userMessage;

    public ErrorResponse(HttpStatus httpStatus, InternalErrorCode errorCode, String devMessage, String langEnMessage, String langRuMessage) {
        this.code = String.valueOf(httpStatus.value());
        this.internalCode = errorCode.getStringValue();
        this.devMessage = devMessage;
        this.userMessage = new UserMessage(langEnMessage, langRuMessage);
    }

}

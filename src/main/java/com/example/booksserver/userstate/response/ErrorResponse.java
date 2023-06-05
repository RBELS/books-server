package com.example.booksserver.userstate.response;

import com.example.booksserver.components.InternalErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    @Getter
    public static class UserMessage {
        @JsonProperty("lang_en")
        private String langEn;
        @JsonProperty("lang_ru")
        private String langRu;

        public UserMessage(String langEnMessage, String langRuMessage) {
            this.langEn = langEnMessage;
            this.langRu = langRuMessage;
        }
    }

    private final String code;
    private final String internalCode;
    private final String devMessage;
    private final UserMessage userMessage;

    public ErrorResponse(HttpStatus httpStatus, InternalErrorCode errorCode, String devMessage, String langEnMessage, String langRuMessage) {
        this.code = String.valueOf(httpStatus.value());
        this.internalCode = errorCode.getStringValue();
        this.devMessage = devMessage;
        this.userMessage = new UserMessage(langEnMessage, langRuMessage);
    }

}

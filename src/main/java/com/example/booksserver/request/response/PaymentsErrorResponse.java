package com.example.booksserver.request.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentsErrorResponse {
    @Data
    public static class UserMessage {
        @JsonProperty("lang_en")
        private String langEn;
        @JsonProperty("lang_ru")
        private String langRu;
    }

    private String code;
    private String internalCode;
    private String devMessage;

}

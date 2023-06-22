package com.example.booksserver.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
public class PaymentsErrorResponse {
    @Data
    @Accessors(chain = true)
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
}

package com.example.booksserver.userstate.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ErrorResponse {
    public static final Map<InternalErrorCode, String> devMessages;
    public static final Map<InternalErrorCode, String> langEnMessages;
    public static final Map<InternalErrorCode, String> langRuMessages;

    static {
        devMessages = new HashMap<>();
        langEnMessages = new HashMap<>();
        langRuMessages = new HashMap<>();

        devMessages.put(InternalErrorCode.BOOK_BAD_AUTHORS, "Authors do not match the provided pattern.");
        langEnMessages.put(InternalErrorCode.BOOK_BAD_AUTHORS, "Authors do not match the provided pattern.");
        langRuMessages.put(InternalErrorCode.BOOK_BAD_AUTHORS, "Информация об авторах не удовлетворяет спецификации.");

        devMessages.put(InternalErrorCode.BOOK_BAD_NAME, "Book name does not match the provided pattern.");
        langEnMessages.put(InternalErrorCode.BOOK_BAD_NAME, "Book name does not match the provided pattern.");
        langRuMessages.put(InternalErrorCode.BOOK_BAD_NAME, "Название книги не удовлетворяет спецификации.");

        devMessages.put(InternalErrorCode.BOOK_BAD_RELEASE_YEAR, "Book release year does not match the provided pattern.");
        langEnMessages.put(InternalErrorCode.BOOK_BAD_RELEASE_YEAR, "Book release year does not match the provided pattern.");
        langRuMessages.put(InternalErrorCode.BOOK_BAD_RELEASE_YEAR, "Год выпуска книги не удовлетворяет спецификации.");

        devMessages.put(InternalErrorCode.BOOK_BAD_PRICE, "Book price does not match the provided pattern.");
        langEnMessages.put(InternalErrorCode.BOOK_BAD_PRICE, "Book price does not match the provided pattern.");
        langRuMessages.put(InternalErrorCode.BOOK_BAD_PRICE, "Цена книги не удовлетворяет спецификации.");

        devMessages.put(InternalErrorCode.BOOK_BAD_IMAGES, "Book images were not provided or some of them have bad format.");
        langEnMessages.put(InternalErrorCode.BOOK_BAD_IMAGES, "Book images were not provided or some of them have bad format.");
        langRuMessages.put(InternalErrorCode.BOOK_BAD_IMAGES, "Переданные изображения не предоставлены либо некоторые из них имеют не подходящий формат.");

        devMessages.put(InternalErrorCode.INTERNAL_ERROR_IMAGES, "Impossible to parse provided images.");
        langEnMessages.put(InternalErrorCode.INTERNAL_ERROR_IMAGES, "Impossible to parse provided images.");
        langRuMessages.put(InternalErrorCode.INTERNAL_ERROR_IMAGES, "Невозможно обработать переданные изображения.");
    }

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

    @Getter
    @AllArgsConstructor
    public static class UserMessage {
        @JsonProperty("lang_en")
        private String langEn;
        @JsonProperty("lang_ru")
        private String langRu;

        public UserMessage(InternalErrorCode errorCode) {
            this.langEn = langEnMessages.get(errorCode);
            this.langRu = langRuMessages.get(errorCode);
        }
    }

    private final String code;
    private final String internalCode;
    private final String devMessage;
    private final UserMessage userMessage;

    public ErrorResponse(HttpStatus httpStatus, InternalErrorCode errorCode) {
        this.code = String.valueOf(httpStatus.value());
        this.internalCode = errorCode.getStringValue();
        this.devMessage = devMessages.get(errorCode);
        this.userMessage = new UserMessage(errorCode);
    }

}

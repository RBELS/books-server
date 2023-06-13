package com.example.booksserver.exception;

import lombok.Getter;

public enum InternalErrorCode {
    BOOK_BAD_AUTHORS(101), BOOK_BAD_NAME(102), BOOK_BAD_RELEASE_YEAR(103), BOOK_BAD_PRICE(104),
    BOOK_BAD_IMAGES(105),

    AUTHOR_BAD_NAME(201),

    ORDER_BAD_EMAIL(301), ORDER_BAD_ADDRESS(302), ORDER_BAD_NAME(303), ORDER_BAD_PHONE(304),
    ORDER_NO_ITEMS(305), ORDER_ITEM_NOT_FOUND(306), ORDER_ITEM_NOT_IN_STOCK(307),
    ORDER_NOT_FOUND(308),

    PAYMENT_ERROR(401), PAYMENT_CANCEL_NOT_ALLOWED(402),

    IMAGE_NOT_FOUND(501),

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

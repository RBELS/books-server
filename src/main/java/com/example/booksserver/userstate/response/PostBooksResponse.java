package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.Book;
import com.example.booksserver.userstate.UserBook;
import lombok.Getter;

@Getter
public class PostBooksResponse {
    private final String status;
    private final UserBook book;

    public PostBooksResponse(Book book, String baseImgUrl) {
        this.status = "SUCCESS";
        this.book = new UserBook(book, baseImgUrl);
    }
}

package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.Book;
import com.example.booksserver.userstate.UserBook;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostBooksResponse {
    private String status;
    private UserBook book;

    public PostBooksResponse(Book book, String baseImgUrl) {
        this.status = "SUCCESS";
        this.book = new UserBook(book, baseImgUrl);
    }
}

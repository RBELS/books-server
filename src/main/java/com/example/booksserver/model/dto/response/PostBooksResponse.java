package com.example.booksserver.model.dto.response;

import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.dto.BookDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostBooksResponse {
    private String status;
    private BookDTO book;

    public PostBooksResponse(Book book, String baseImgUrl) {
        this.status = "SUCCESS";
        this.book = new BookDTO(book, baseImgUrl);
    }
}

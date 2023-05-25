package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.userstate.UserBook;
import com.example.booksserver.userstate.filters.BooksFilters;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Getter
public class GetBooksResponse extends PaginatedResponse {
    @Getter
    public static class ResponseFilters {
        private final BigDecimal minPrice;
        private final BigDecimal maxPrice;
        private final List<Long> authors;

        public ResponseFilters(BooksFilters booksFilters) {
            this.minPrice = booksFilters.getMinPrice();
            this.maxPrice = booksFilters.getMaxPrice();
            this.authors = booksFilters.getAuthorIdList();
        }
    }

    private final ResponseFilters filters;
    private final List<UserBook> content;

    public GetBooksResponse(BooksFilters booksFilters, Page<BookDTO> content, String baseImgUrl) {
        super(content);
        this.filters = new ResponseFilters(booksFilters);
        this.content = content.stream().map(bookDTO -> new UserBook(bookDTO, baseImgUrl)).toList();
    }
}

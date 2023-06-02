package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.Book;
import com.example.booksserver.userstate.UserBook;
import com.example.booksserver.userstate.filters.BooksFilters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetBooksResponse extends PaginatedResponse {
    @Data
    @NoArgsConstructor
    public static class ResponseFilters {
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private List<Long> authors;

        public ResponseFilters(BooksFilters booksFilters) {
            this.minPrice = booksFilters.getMinPrice();
            this.maxPrice = booksFilters.getMaxPrice();
            this.authors = booksFilters.getAuthorIdList();
        }
    }

    private ResponseFilters filters;
    private List<UserBook> content;

    public GetBooksResponse(BooksFilters booksFilters, Page<Book> content, String baseImgUrl) {
        super(content);
        this.filters = new ResponseFilters(booksFilters);
        this.content = content.stream().map(bookDTO -> new UserBook(bookDTO, baseImgUrl)).toList();
    }
}

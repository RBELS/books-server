package com.example.booksserver.userstate.response;

import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.userstate.UserBook;
import com.example.booksserver.userstate.filters.BooksFilters;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class GetBooksResponse extends PaginatedResponse {
    @Getter
    public static class ResponseFilters {
        private final Double minPrice;
        private final Double maxPrice;
        private final List<Long> authors;

        public ResponseFilters(BooksFilters booksFilters) {
            this.minPrice = Objects.isNull(booksFilters.getMinPrice()) ? null : booksFilters.getMinPrice() / 100.0;
            this.maxPrice = Objects.isNull(booksFilters.getMaxPrice()) ? null : booksFilters.getMaxPrice() / 100.0;
            this.authors = booksFilters.getAuthorIdList();
        }
    }

    private final ResponseFilters filters;
    private final List<UserBook> content;

    public GetBooksResponse(BooksFilters booksFilters, List<BookDTO> content, String baseImgUrl) {
        super(booksFilters);
        this.filters = new ResponseFilters(booksFilters);
        this.content = content.stream().map(bookDTO -> new UserBook(bookDTO, baseImgUrl)).toList();
    }
}

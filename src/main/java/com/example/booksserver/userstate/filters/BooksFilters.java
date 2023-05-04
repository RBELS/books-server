package com.example.booksserver.userstate.filters;

import lombok.Getter;

import java.util.Objects;

@Getter
public class BooksFilters extends PaginationFilters {
    public static final int DEFAULT_COUNT_PER_PAGE = 20;

    private final Long authorId;
    private final Long minPrice;
    private final Long maxPrice;

    public BooksFilters(Long authorId, Double minPrice, Double maxPrice, Integer page, Integer count) {
        super(page, count);
        this.authorId = authorId;
        this.minPrice = minPrice == null ? null : (long) (minPrice * 100);
        this.maxPrice = maxPrice == null ? null : (long) (maxPrice * 100);
    }
}

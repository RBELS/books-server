package com.example.booksserver.userstate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Filters {
    private final Long authorId;
    private final Long minPrice;
    private final Long maxPrice;

    private final Integer page;
    private final Integer count;

    public Filters(Long authorId, Double minPrice, Double maxPrice, Integer page, Integer count) {
        this.authorId = authorId;
        this.minPrice = minPrice == null ? null : (long) (minPrice * 100);
        this.maxPrice = maxPrice == null ? null : (long) (maxPrice * 100);
        this.page = Math.max(page-1, 0);
        this.count = Math.max(count, 0);
    }
}

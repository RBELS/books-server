package com.example.booksserver.userstate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Filters {
    private final Long authorId;
    private final Long minPrice;
    private final Long maxPrice;

    public Filters(Long authorId, Double minPrice, Double maxPrice) {
        this.authorId = authorId;
        this.minPrice = minPrice == null ? null : (long) (minPrice * 100);
        this.maxPrice = maxPrice == null ? null : (long) (maxPrice * 100);
    }
}

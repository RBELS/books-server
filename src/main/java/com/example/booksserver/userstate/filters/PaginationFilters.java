package com.example.booksserver.userstate.filters;

import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class PaginationFilters {
    private final Integer page;
    private final Integer count;

    public PaginationFilters(Integer page, Integer count) {
        this.page = Objects.isNull(page) ? null : Math.max(page-1, 0);
        this.count = Objects.isNull(page) ? null : Math.max(count, 0);
    }
}

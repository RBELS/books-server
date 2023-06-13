package com.example.booksserver.model.dto.filters;

import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class PaginatedFilters {
    private final Integer page;
    private final Integer count;

    public PaginatedFilters(Integer page, Integer count) {
        this.page = Objects.isNull(page) ? 0 : Math.max(page-1, 0);
        if (Objects.isNull(count) || count < 0) {
            this.count = getDefaultCount();
        } else {
            this.count = count;
        }
    }

    protected abstract int getDefaultCount();
}

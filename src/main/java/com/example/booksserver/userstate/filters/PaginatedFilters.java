package com.example.booksserver.userstate.filters;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
public abstract class PaginatedFilters {
    private final Integer page;
    private final Integer count;

    @Getter @Setter
    private Integer outTotalPages;

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

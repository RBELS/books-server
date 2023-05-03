package com.example.booksserver.userstate.filters;

import lombok.Getter;

import java.util.Objects;

@Getter
public class AuthorsFilters {
    private final Integer page;
    private final Integer count;

    public AuthorsFilters(Integer page, Integer count) {
        this.page = Objects.isNull(page) ? null : Math.max(page-1, 0);
        this.count = Objects.isNull(page) ? null : Math.max(count, 0);
    }
}

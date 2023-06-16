package com.example.booksserver.model.dto.filters;

import lombok.Getter;


@Getter
public class AuthorsFilters extends PaginatedFilters {
    public static final int DEFAULT_COUNT = 20;

    public AuthorsFilters(Integer page, Integer count) {
        super(page, count);
    }

    @Override
    protected int getDefaultCount() {
        return DEFAULT_COUNT;
    }
}

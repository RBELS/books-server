package com.example.booksserver.userstate.response;

import com.example.booksserver.userstate.filters.PaginatedFilters;
import lombok.Getter;

@Getter
public abstract class PaginatedResponse {
    private final Integer page;
    private final Integer count;
    private final Integer totalPages;

    public PaginatedResponse(PaginatedFilters filters) {
        this.page = filters.getPage() + 1;
        this.count = filters.getCount();
        this.totalPages = filters.getOutTotalPages();
    }
}

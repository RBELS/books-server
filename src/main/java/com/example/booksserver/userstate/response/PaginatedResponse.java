package com.example.booksserver.userstate.response;

import com.example.booksserver.userstate.filters.PaginatedFilters;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public abstract class PaginatedResponse {
    private final Integer page;
    private final Integer count;
    private final Integer totalPages;

    public PaginatedResponse(Page<?> page) {
        this.page = page.getNumber() + 1;
        this.count = page.getSize();
        this.totalPages = page.getTotalPages();
    }
}

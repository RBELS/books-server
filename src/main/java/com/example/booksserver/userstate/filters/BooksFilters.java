package com.example.booksserver.userstate.filters;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class BooksFilters extends PaginatedFilters {
    public static final int DEFAULT_COUNT_PER_PAGE = 20;

    private final List<Long> authorIdList;
    private final Long minPrice;
    private final Long maxPrice;

    public BooksFilters(List<Long> authorIdList, Double minPrice, Double maxPrice, Integer page, Integer count) {
        super(page, count);
        this.authorIdList = new ArrayList<>();
        if (!Objects.isNull(authorIdList)) {
            this.authorIdList.addAll(authorIdList);
        }
        this.minPrice = minPrice == null ? null : (long) (minPrice * 100);
        this.maxPrice = maxPrice == null ? null : (long) (maxPrice * 100);
    }

    @Override
    protected int getDefaultCount() {
        return 20;
    }
}

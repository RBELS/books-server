package com.example.booksserver.model.dto.filters;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class BooksFilters extends PaginatedFilters {
    public static final int DEFAULT_COUNT_PER_PAGE = 20;

    private final List<Long> authorIdList;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;

    public BooksFilters(List<Long> authorIdList, BigDecimal minPrice, BigDecimal maxPrice, Integer page, Integer count) {
        super(page, count);
        this.authorIdList = new ArrayList<>();
        if (!Objects.isNull(authorIdList)) {
            this.authorIdList.addAll(authorIdList);
        }
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }

    @Override
    protected int getDefaultCount() {
        return 20;
    }
}

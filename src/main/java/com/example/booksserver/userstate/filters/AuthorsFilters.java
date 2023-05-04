package com.example.booksserver.userstate.filters;

import lombok.Getter;


@Getter
public class AuthorsFilters extends PaginationFilters {

    public AuthorsFilters(Integer page, Integer count) {
        super(page, count);
    }
}

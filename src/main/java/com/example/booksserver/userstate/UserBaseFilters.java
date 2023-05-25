package com.example.booksserver.userstate;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
public class UserBaseFilters {
    private final List<BigDecimal> pricesRange;
}

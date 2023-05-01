package com.example.booksserver.userstate;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UserBaseFilters {
    private final List<Double> pricesRange;
}

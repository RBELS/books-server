package com.example.booksserver.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserBaseFiltersResponse {
    private List<BigDecimal> pricesRange;
}

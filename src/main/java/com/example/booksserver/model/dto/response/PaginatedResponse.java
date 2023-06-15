package com.example.booksserver.model.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
public abstract class PaginatedResponse {
    private Integer page;
    private Integer count;
    private Integer totalPages;

    public PaginatedResponse(Page<?> page) {
        this.page = page.getNumber() + 1;
        this.count = page.getSize();
        this.totalPages = page.getTotalPages();
    }
}

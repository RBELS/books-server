package com.example.booksserver.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class PostBooksRequest {
    @JsonProperty("authors")
    private List<Long> authorIdList;

    @JsonProperty("name")
    private String bookName;

    private Integer releaseYear;
    private BigDecimal price;
}

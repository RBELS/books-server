package com.example.booksserver.model.service;

import com.example.booksserver.model.entity.CancelStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class OrderCancelStatus {
    private Long id;
    private CancelStatus status;
    private LocalDateTime dateRequested;
}

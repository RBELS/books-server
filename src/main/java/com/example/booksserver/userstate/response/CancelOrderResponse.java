package com.example.booksserver.userstate.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CancelOrderResponse {
    private String status;
    private String orderNo;
}

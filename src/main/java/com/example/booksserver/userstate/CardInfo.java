package com.example.booksserver.userstate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class CardInfo {
    private String number;
    private String cvv;
    private String name;
}

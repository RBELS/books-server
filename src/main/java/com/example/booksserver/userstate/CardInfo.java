package com.example.booksserver.userstate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CardInfo {
    private String number;
    private String cvv;
    private String name;
}

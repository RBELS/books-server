package com.example.booksserver.service;

import com.example.booksserver.dto.Order;
import com.example.booksserver.userstate.CardInfo;

public interface IOrderTransactionService {
    Order saveOrder(Order order, boolean doSaveStock);
    Order processPayment(Order order, CardInfo cardInfo);
    Order processCancelPayment(Order order);
}

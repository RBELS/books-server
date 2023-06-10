package com.example.booksserver.schedule;

import com.example.booksserver.dto.Order;
import com.example.booksserver.entity.order.OrderStatus;
import com.example.booksserver.map.OrderMapper;
import com.example.booksserver.map.StockMapper;
import com.example.booksserver.repository.OrderRepository;
import com.example.booksserver.repository.StockRepository;
import com.example.booksserver.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduleTransactionService implements IPaymentScheduleTransactionService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final StockMapper stockMapper;


    // Should I use a static method from the IOrderService class?
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    @Override
    public void saveOrderWithRollback(Order order, OrderStatus resultStatus, boolean doMoveStock) {
        order = orderMapper.entityToDto(
                orderRepository.findById(order.getId())
                        .orElseThrow()
        );

        if (doMoveStock) {
            IOrderService.moveStock(order, true);
            order.getOrderItems().forEach(orderItem -> stockRepository.save(stockMapper.dtoToEntity(orderItem.getBook().getStock())));
        }

        order.setStatus(resultStatus);
        orderRepository.save(
                orderMapper.dtoToEntity(order)
        );
    }
}

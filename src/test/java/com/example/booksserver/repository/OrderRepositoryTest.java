package com.example.booksserver.repository;

import com.example.booksserver.entity.order.Order;
import com.example.booksserver.entity.order.OrderItem;
import com.example.booksserver.entity.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    public static final int GEN_COUNT = 100;
    public static void insertOrders(OrderRepository orderRepository, int count) {
        for (int i = 0;i < count;i++) {
            Order order = new Order()
                    .setName("Order name")
                    .setPhone("Order phone")
                    .setEmail("Order email")
                    .setAddress("Order address")
                    .setStatus(
                            OrderStatus.values()[(int) (Math.random() * OrderStatus.values().length)]
                    );
            orderRepository.save(order);
        }
    }

    @BeforeEach
    public void init() {
        insertOrders(orderRepository, GEN_COUNT);
    }

    @Test
    public void findAllByStatus() {
        OrderStatus orderStatus = OrderStatus.PENDING;
        List<Order> expected = orderRepository
                .findAll()
                .stream()
                .filter(order -> order.getStatus() == orderStatus)
                .toList();
        List<Order> actual = orderRepository.findAllByStatus(orderStatus);

        assertThat(expected).isEqualTo(actual);
    }
}

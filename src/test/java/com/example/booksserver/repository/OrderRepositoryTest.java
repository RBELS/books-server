package com.example.booksserver.repository;

import com.example.booksserver.model.entity.OrderEntity;
import com.example.booksserver.model.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    public static final int GEN_COUNT = 100;
    public static void insertOrders(OrderRepository orderRepository, int count) {
        for (int i = 0;i < count;i++) {
            OrderEntity orderEntity = new OrderEntity()
                    .setName("Order name")
                    .setPhone("Order phone")
                    .setEmail("Order email")
                    .setAddress("Order address")
                    .setStatus(
                            OrderStatus.values()[(int) (Math.random() * OrderStatus.values().length)]
                    );
            orderRepository.save(orderEntity);
        }
    }

    @BeforeEach
    public void init() {
        insertOrders(orderRepository, GEN_COUNT);
    }

    @Test
    public void findAllByStatus() {
        List<OrderStatus> orderStatusList = Arrays.asList(OrderStatus.PENDING);
        List<OrderEntity> expected = orderRepository
                .findAll()
                .stream()
                .filter(order -> orderStatusList.contains(order.getStatus()))
                .toList();
        List<OrderEntity> actual = orderRepository.findAllByStatusIn(orderStatusList);

        assertThat(expected).isEqualTo(actual);
    }
}

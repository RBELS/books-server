package com.example.booksserver.map;

import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderMapperTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private OrderMapper orderMapper;

    private final OrderEntity someEntity;
    private final Order someServiceObj;

    {
        someEntity = new OrderEntity()
                .setId(10L)
                .setName("Some person")
                .setPhone("Some phone")
                .setEmail("Some email")
                .setAddress("Some address")
                .setDateCreated(LocalDateTime.now());
        someServiceObj = new Order()
                .setId(10L)
                .setName("Some person")
                .setPhone("Some phone")
                .setEmail("Some email")
                .setAddress("Some address")
                .setDateCreated(LocalDateTime.now());
    }

    private void compareEntityToService(OrderEntity entity, Order dto) {
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getPhone()).isEqualTo(dto.getPhone());
        assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
        assertThat(entity.getAddress()).isEqualTo(dto.getAddress());
        assertThat(entity.getDateCreated()).isEqualTo(dto.getDateCreated());
    }

    @Test
    void entityToService() {
        Order dto = orderMapper.entityToService(someEntity);
        compareEntityToService(someEntity, dto);
    }

    @Test
    void serviceToEntity() {
        OrderEntity entity = orderMapper.serviceToEntity(someServiceObj);
        compareEntityToService(entity, someServiceObj);
    }

    private void compareEntityToServiceList(List<OrderEntity> entityList, List<Order> dtoList) {
        assertThat(entityList).hasSameSizeAs(dtoList);

        for (int i = 0;i < entityList.size();i++) {
            compareEntityToService(entityList.get(i), dtoList.get(i));
        }
    }

    @Test
    void entityToServiceList() {
        List<OrderEntity> entityList = Arrays.asList(someEntity, someEntity, someEntity);
        List<Order> dtoList = orderMapper.entityToService(entityList);

        compareEntityToServiceList(entityList, dtoList);
    }

    @Test
    void serviceToEntityList() {
        List<Order> dtoList = Arrays.asList(someServiceObj, someServiceObj, someServiceObj);
        List<OrderEntity> entityList = orderMapper.serviceToEntity(dtoList);

        compareEntityToServiceList(entityList, dtoList);
    }
}
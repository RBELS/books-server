package com.example.booksserver.map;

import com.example.booksserver.model.service.OrderItem;
import com.example.booksserver.model.entity.OrderItemEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderItemMapperTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private OrderItemMapper orderItemMapper;

    private final OrderItemEntity someEntity;
    private final OrderItem someDto;

    {
        someEntity = new OrderItemEntity()
                .setId(10L)
                .setPrice(new BigDecimal("10.00"))
                .setCount(2);
        someDto = new OrderItem()
                .setId(10L)
                .setPrice(new BigDecimal("10.00"))
                .setCount(2);
    }

    private void compareEntityToService(OrderItemEntity entity, OrderItem serviceObj) {
        assertThat(entity.getId()).isEqualTo(serviceObj.getId());
        assertThat(entity.getCount()).isEqualTo(serviceObj.getCount());
        assertThat(entity.getPrice()).isEqualTo(serviceObj.getPrice());
    }

    @Test
    void entityToService() {
        OrderItem serviceObj = orderItemMapper.entityToService(someEntity);
        compareEntityToService(someEntity, serviceObj);
    }

    @Test
    void serviceToEntity() {
        OrderItemEntity entity = orderItemMapper.serviceToEntity(someDto);
        compareEntityToService(entity, someDto);
    }

    private void compareEntityToServiceList(List<OrderItemEntity> entityList, List<OrderItem> serviceList) {
        assertThat(entityList).hasSameSizeAs(serviceList);

        for (int i = 0;i < entityList.size();i++) {
            compareEntityToService(entityList.get(i), serviceList.get(i));
        }
    }

    @Test
    void entityToServiceList() {
        List<OrderItemEntity> entityList = Arrays.asList(someEntity, someEntity, someEntity);
        List<OrderItem> dtoList = orderItemMapper.entityToService(entityList);

        compareEntityToServiceList(entityList, dtoList);
    }

    @Test
    void dtoToEntityList() {
        List<OrderItem> dtoList = Arrays.asList(someDto, someDto, someDto);
        List<OrderItemEntity> entityList = orderItemMapper.serviceToEntity(dtoList);

        compareEntityToServiceList(entityList, dtoList);
    }
}
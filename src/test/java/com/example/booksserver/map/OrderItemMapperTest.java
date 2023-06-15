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

    private void compareEntityToDto(OrderItemEntity entity, OrderItem dto) {
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getCount()).isEqualTo(dto.getCount());
        assertThat(entity.getPrice()).isEqualTo(dto.getPrice());
    }

    @Test
    void entityToDto() {
        OrderItem dto = orderItemMapper.entityToDto(someEntity);
        compareEntityToDto(someEntity, dto);
    }

    @Test
    void dtoToEntity() {
        OrderItemEntity entity = orderItemMapper.dtoToEntity(someDto);
        compareEntityToDto(entity, someDto);
    }

    private void compareEntityToDtoList(List<OrderItemEntity> entityList, List<OrderItem> dtoList) {
        assertThat(entityList).hasSameSizeAs(dtoList);

        for (int i = 0;i < entityList.size();i++) {
            compareEntityToDto(entityList.get(i), dtoList.get(i));
        }
    }

    @Test
    void entityToDtoList() {
        List<OrderItemEntity> entityList = Arrays.asList(someEntity, someEntity, someEntity);
        List<OrderItem> dtoList = orderItemMapper.entityToDto(entityList);

        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void dtoToEntityList() {
        List<OrderItem> dtoList = Arrays.asList(someDto, someDto, someDto);
        List<OrderItemEntity> entityList = orderItemMapper.dtoToEntity(dtoList);

        compareEntityToDtoList(entityList, dtoList);
    }
}
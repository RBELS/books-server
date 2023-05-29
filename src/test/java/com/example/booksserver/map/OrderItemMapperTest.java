package com.example.booksserver.map;

import com.example.booksserver.dto.OrderItemDTO;
import com.example.booksserver.entity.OrderItem;
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

    private final OrderItem someEntity;
    private final OrderItemDTO someDto;

    {
        someEntity = new OrderItem()
                .setId(10L)
                .setPrice(new BigDecimal("10.00"))
                .setCount(2);
        someDto = new OrderItemDTO()
                .setId(10L)
                .setPrice(new BigDecimal("10.00"))
                .setCount(2);
    }

    private void compareEntityToDto(OrderItem entity, OrderItemDTO dto) {
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getCount()).isEqualTo(dto.getCount());
        assertThat(entity.getPrice()).isEqualTo(dto.getPrice());
    }

    @Test
    void entityToDto() {
        OrderItemDTO dto = orderItemMapper.entityToDto(someEntity);
        compareEntityToDto(someEntity, dto);
    }

    @Test
    void dtoToEntity() {
        OrderItem entity = orderItemMapper.dtoToEntity(someDto);
        compareEntityToDto(entity, someDto);
    }

    private void compareEntityToDtoList(List<OrderItem> entityList, List<OrderItemDTO> dtoList) {
        assertThat(entityList).hasSameSizeAs(dtoList);

        for (int i = 0;i < entityList.size();i++) {
            compareEntityToDto(entityList.get(i), dtoList.get(i));
        }
    }

    @Test
    void entityToDtoList() {
        List<OrderItem> entityList = Arrays.asList(someEntity, someEntity, someEntity);
        List<OrderItemDTO> dtoList = orderItemMapper.entityToDto(entityList);

        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void dtoToEntityList() {
        List<OrderItemDTO> dtoList = Arrays.asList(someDto, someDto, someDto);
        List<OrderItem> entityList = orderItemMapper.dtoToEntity(dtoList);

        compareEntityToDtoList(entityList, dtoList);
    }
}
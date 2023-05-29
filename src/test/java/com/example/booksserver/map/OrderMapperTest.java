package com.example.booksserver.map;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderMapperTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private OrderMapper orderMapper;

    private final Order someEntity;
    private final OrderDTO someDto;

    {
        someEntity = new Order()
                .setId(10L)
                .setName("Some person")
                .setPhone("Some phone")
                .setEmail("Some email")
                .setAddress("Some address")
                .setDateCreated(new Date(System.currentTimeMillis()));
        someDto = new OrderDTO()
                .setId(10L)
                .setName("Some person")
                .setPhone("Some phone")
                .setEmail("Some email")
                .setAddress("Some address")
                .setDateCreated(new Date(System.currentTimeMillis()));
    }

    private void compareEntityToDto(Order entity, OrderDTO dto) {
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getPhone()).isEqualTo(dto.getPhone());
        assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
        assertThat(entity.getAddress()).isEqualTo(dto.getAddress());
        assertThat(entity.getDateCreated()).isEqualTo(dto.getDateCreated());
    }

    @Test
    void entityToDto() {
        OrderDTO dto = orderMapper.entityToDto(someEntity);
        compareEntityToDto(someEntity, dto);
    }

    @Test
    void dtoToEntity() {
        Order entity = orderMapper.dtoToEntity(someDto);
        compareEntityToDto(entity, someDto);
    }

    private void compareEntityToDtoList(List<Order> entityList, List<OrderDTO> dtoList) {
        assertThat(entityList).hasSameSizeAs(dtoList);

        for (int i = 0;i < entityList.size();i++) {
            compareEntityToDto(entityList.get(i), dtoList.get(i));
        }
    }

    @Test
    void entityToDtoList() {
        List<Order> entityList = Arrays.asList(someEntity, someEntity, someEntity);
        List<OrderDTO> dtoList = orderMapper.entityToDto(entityList);

        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void dtoToEntityList() {
        List<OrderDTO> dtoList = Arrays.asList(someDto, someDto, someDto);
        List<Order> entityList = orderMapper.dtoToEntity(dtoList);

        compareEntityToDtoList(entityList, dtoList);
    }
}
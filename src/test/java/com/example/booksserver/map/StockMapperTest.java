package com.example.booksserver.map;

import com.example.booksserver.model.service.Stock;
import com.example.booksserver.model.entity.StockEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class StockMapperTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private StockMapper stockMapper;

    private final StockEntity someEntity;
    private final Stock someDto;

    {
        someEntity = new StockEntity()
                .setId(10L)
                .setAvailable(10)
                .setOrdered(5)
                .setInDelivery(2);
        someDto = new Stock()
                .setId(10L)
                .setAvailable(10)
                .setOrdered(5)
                .setInDelivery(2);
    }

    private void compareEntityToDto(StockEntity entity, Stock dto) {
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getAvailable()).isEqualTo(dto.getAvailable());
        assertThat(entity.getOrdered()).isEqualTo(dto.getOrdered());
        assertThat(entity.getInDelivery()).isEqualTo(dto.getInDelivery());
    }

    @Test
    void entityToDto() {
        Stock dto = stockMapper.entityToDto(someEntity);
        compareEntityToDto(someEntity, dto);
    }

    @Test
    void dtoToEntity() {
        StockEntity entity = stockMapper.dtoToEntity(someDto);
        compareEntityToDto(entity, someDto);
    }

    private void compareEntityToDtoList(List<StockEntity> entityList, List<Stock> dtoList) {
        assertThat(entityList).hasSameSizeAs(dtoList);

        for (int i = 0;i < entityList.size();i++) {
            compareEntityToDto(entityList.get(i), dtoList.get(i));
        }
    }

    @Test
    void entityToDtoList() {
        List<StockEntity> entityList = Arrays.asList(someEntity, someEntity, someEntity);
        List<Stock> dtoList = stockMapper.entityToDto(entityList);

        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void dtoToEntityList() {
        List<Stock> dtoList = Arrays.asList(someDto, someDto, someDto);
        List<StockEntity> entityList = stockMapper.dtoToEntity(dtoList);

        compareEntityToDtoList(entityList, dtoList);
    }
}
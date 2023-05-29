package com.example.booksserver.map;

import com.example.booksserver.dto.StockDTO;
import com.example.booksserver.entity.Stock;
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

    private final Stock someEntity;
    private final StockDTO someDto;

    {
        someEntity = new Stock()
                .setId(10L)
                .setAvailable(10)
                .setOrdered(5)
                .setInDelivery(2);
        someDto = new StockDTO()
                .setId(10L)
                .setAvailable(10)
                .setOrdered(5)
                .setInDelivery(2);
    }

    private void compareEntityToDto(Stock entity, StockDTO dto) {
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getAvailable()).isEqualTo(dto.getAvailable());
        assertThat(entity.getOrdered()).isEqualTo(dto.getOrdered());
        assertThat(entity.getInDelivery()).isEqualTo(dto.getInDelivery());
    }

    @Test
    void entityToDto() {
        StockDTO dto = stockMapper.entityToDto(someEntity);
        compareEntityToDto(someEntity, dto);
    }

    @Test
    void dtoToEntity() {
        Stock entity = stockMapper.dtoToEntity(someDto);
        compareEntityToDto(entity, someDto);
    }

    private void compareEntityToDtoList(List<Stock> entityList, List<StockDTO> dtoList) {
        assertThat(entityList).hasSameSizeAs(dtoList);

        for (int i = 0;i < entityList.size();i++) {
            compareEntityToDto(entityList.get(i), dtoList.get(i));
        }
    }

    @Test
    void entityToDtoList() {
        List<Stock> entityList = Arrays.asList(someEntity, someEntity, someEntity);
        List<StockDTO> dtoList = stockMapper.entityToDto(entityList);

        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void dtoToEntityList() {
        List<StockDTO> dtoList = Arrays.asList(someDto, someDto, someDto);
        List<Stock> entityList = stockMapper.dtoToEntity(dtoList);

        compareEntityToDtoList(entityList, dtoList);
    }
}
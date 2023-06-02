package com.example.booksserver.map;

import com.example.booksserver.dto.Stock;
import com.example.booksserver.entity.StockEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class StockMapper {
    public abstract Stock entityToDto(StockEntity entity);
    public abstract StockEntity dtoToEntity(Stock dto);
    public abstract List<Stock> entityToDto(List<StockEntity> entity);
    public abstract List<StockEntity> dtoToEntity(List<Stock> dto);
}

package com.example.booksserver.map;

import com.example.booksserver.dto.StockDTO;
import com.example.booksserver.entity.Stock;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class StockMapper {
    public abstract StockDTO entityToDto(Stock entity);
    public abstract Stock dtoToEntity(StockDTO dto);

    public abstract List<StockDTO> entityToDto(List<Stock> entity);
    public abstract List<Stock> dtoToEntity(List<StockDTO> dto);
}

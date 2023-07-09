package com.example.booksserver.map;

import com.example.booksserver.model.service.Stock;
import com.example.booksserver.model.entity.StockEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class StockMapper {
    public abstract Stock entityToService(StockEntity entity);
    public abstract StockEntity serviceToEntity(Stock serviceObj);
    public abstract List<Stock> entityToService(List<StockEntity> entity);
    public abstract List<StockEntity> serviceToEntity(List<Stock> serviceObjList);
}

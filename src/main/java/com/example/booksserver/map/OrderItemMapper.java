package com.example.booksserver.map;

import com.example.booksserver.model.service.OrderItem;
import com.example.booksserver.model.entity.OrderItemEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public abstract class OrderItemMapper {
    public abstract OrderItem entityToService(OrderItemEntity entity);
    public abstract OrderItemEntity serviceToEntity(OrderItem serviceObj);
    public abstract List<OrderItem> entityToService(List<OrderItemEntity> entity);
    public abstract List<OrderItemEntity> serviceToEntity(List<OrderItem> serviceObjList);

}

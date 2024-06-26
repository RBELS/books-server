package com.example.booksserver.map;

import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderItemMapper.class, OrderCancelStatusMapper.class})
public abstract class OrderMapper {
    public abstract Order entityToService(OrderEntity entity);
    public abstract OrderEntity serviceToEntity(Order serviceObj);
    public abstract List<Order> entityToService(List<OrderEntity> entity);
    public abstract List<OrderEntity> serviceToEntity(List<Order> serviceObjList);
}

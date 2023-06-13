package com.example.booksserver.map;

import com.example.booksserver.model.service.Order;
import com.example.booksserver.model.entity.OrderEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public abstract class OrderMapper {
    public abstract Order entityToDto(OrderEntity entity);
    public abstract OrderEntity dtoToEntity(Order dto);
    public abstract List<Order> entityToDto(List<OrderEntity> entity);
    public abstract List<OrderEntity> dtoToEntity(List<Order> dto);
}

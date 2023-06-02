package com.example.booksserver.map;

import com.example.booksserver.dto.OrderItem;
import com.example.booksserver.entity.order.OrderItemEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public abstract class OrderItemMapper {
    public abstract OrderItem entityToDto(OrderItemEntity entity);
    public abstract OrderItemEntity dtoToEntity(OrderItem dto);
    public abstract List<OrderItem> entityToDto(List<OrderItemEntity> entity);
    public abstract List<OrderItemEntity> dtoToEntity(List<OrderItem> dto);

}

package com.example.booksserver.map;

import com.example.booksserver.dto.OrderItemDTO;
import com.example.booksserver.entity.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public abstract class OrderItemMapper {
    public abstract OrderItemDTO entityToDto(OrderItem entity);
    public abstract OrderItem dtoToEntity(OrderItemDTO dto);

    public abstract List<OrderItemDTO> entityToDto(List<OrderItem> entity);
    public abstract List<OrderItem> dtoToEntity(List<OrderItemDTO> dto);

}

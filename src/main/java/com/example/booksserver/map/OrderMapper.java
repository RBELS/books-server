package com.example.booksserver.map;

import com.example.booksserver.dto.OrderDTO;
import com.example.booksserver.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public abstract class OrderMapper {
    public abstract OrderDTO entityToDto(Order entity);
    public abstract Order dtoToEntity(OrderDTO dto);
    public abstract List<OrderDTO> entityToDto(List<Order> entity);
    public abstract List<Order> dtoToEntity(List<OrderDTO> dto);
}

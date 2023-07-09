package com.example.booksserver.map;

import com.example.booksserver.model.entity.OrderCancelStatusEntity;
import com.example.booksserver.model.service.OrderCancelStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class OrderCancelStatusMapper {
    public abstract OrderCancelStatus entityToService(OrderCancelStatusEntity entity);
    public abstract OrderCancelStatusEntity serviceToEntity(OrderCancelStatus serviceObj);
    public abstract List<OrderCancelStatus> entityToService(List<OrderCancelStatusEntity> entity);
    public abstract List<OrderCancelStatusEntity> serviceToEntity(List<OrderCancelStatus> serviceObjList);
}

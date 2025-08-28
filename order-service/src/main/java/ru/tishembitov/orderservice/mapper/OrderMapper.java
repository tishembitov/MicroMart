package ru.tishembitov.orderservice.mapper;

import ru.tishembitov.orderservice.dto.OrderCreateDto;
import ru.tishembitov.orderservice.dto.OrderDto;
import ru.tishembitov.orderservice.entity.Order;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    Order toEntity(
            OrderCreateDto orderCreateDto,
            final Long userId
    );

    OrderDto toDto(Order order);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Order partialUpdate(
            OrderDto orderDto,
            @MappingTarget
            Order order
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Order partialUpdate(
            OrderCreateDto orderCreateDto,
            @MappingTarget
            Order order
    );
}



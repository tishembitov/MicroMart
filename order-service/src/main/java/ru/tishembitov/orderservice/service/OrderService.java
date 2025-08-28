package ru.tishembitov.orderservice.service;

import ru.tishembitov.orderservice.dto.OrderCreateDto;
import ru.tishembitov.orderservice.dto.OrderDto;

public interface OrderService {
    OrderDto addOne(OrderCreateDto orderCreateDto);

    OrderDto getOne(Long orderId);
}



package ru.tishembitov.reviewsservice.service;

import ru.tishembitov.reviewsservice.dto.OrderDto;

import java.util.Optional;

public interface OrderServiceClient {

    Optional<OrderDto> getOrder(Long orderId);
}

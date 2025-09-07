package ru.tishembitov.orderservice.service;


import ru.tishembitov.orderservice.dto.OrderProductCreateDto;
import ru.tishembitov.orderservice.dto.ProductDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductServiceClient {
    Optional<ProductDto> getProductById(Long id);

    Map<Long, ProductDto> getProductById(List<Long> ids);

    boolean updateStock(List<OrderProductCreateDto> ids);
}

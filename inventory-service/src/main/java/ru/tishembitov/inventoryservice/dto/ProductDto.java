package ru.tishembitov.inventoryservice.dto;

import lombok.Builder;


import java.math.BigDecimal;

@Builder
public record ProductDto(
        Long id,
        String name,
        BigDecimal price,
        int quantity,
        String description
)
{
}
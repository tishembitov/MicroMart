package ru.tishembitov.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

 
import java.time.LocalDateTime;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record OrderDto(
        Long id,
        Long userId,
        List<OrderProductDto> products,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
)
{
}
package ru.tishembitov.reviewsservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;


import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record OrderProductDto(
        Long productId,
        int quantity,
        BigDecimal price
)
{
}
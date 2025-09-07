package ru.tishembitov.reviewsservice.dto;

 
import java.math.BigDecimal;

public record ProductAvgRatDto(
        Long productId,
        BigDecimal avg
)
{
}
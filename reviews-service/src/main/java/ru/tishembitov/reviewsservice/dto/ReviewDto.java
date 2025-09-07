package ru.tishembitov.reviewsservice.dto;

import lombok.Builder;

 
import java.time.LocalDateTime;

@Builder
public record ReviewDto(
        Long id,
        Long userId,
        Long productId,
        Long orderId,
        String reviewText,
        LocalDateTime createdAt,
        int rating
)
{
}
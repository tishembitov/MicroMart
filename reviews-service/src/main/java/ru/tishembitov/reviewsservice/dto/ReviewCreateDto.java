package ru.tishembitov.reviewsservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;


@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record ReviewCreateDto(
        @NotNull
        Long productId,
        @NotNull
        Long orderId,
        @NotBlank
        String reviewText,
        @Range(min = 0,
               max = 5)
        int rating
)
{
}
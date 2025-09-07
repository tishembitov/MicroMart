package ru.tishembitov.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;


@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record OrderProductCreateDto(
        @NotNull Long productId,
        @Positive int quantity
)
{
}
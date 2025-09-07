package ru.tishembitov.inventoryservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record ProductStockQuantityDto(
        @NotNull
        Long productId,
        @NotNull
        @Positive
        int quantity
)
{
}
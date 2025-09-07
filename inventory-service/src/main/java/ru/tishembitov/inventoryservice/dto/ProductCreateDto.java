package ru.tishembitov.inventoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

 
import java.math.BigDecimal;

@Builder
public record ProductCreateDto(
        @NotBlank
        String name,
        @NotNull
        @Positive(message = "Only positive number allowed")
        BigDecimal price,
        @Positive(message = "Only positive number allowed")
        int quantity,
        String description
)
{
}
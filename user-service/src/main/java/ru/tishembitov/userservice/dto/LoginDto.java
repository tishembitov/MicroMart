package ru.tishembitov.userservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;



@Builder
public record LoginDto(
        String username,
        @NotNull
        String password
)
{
}
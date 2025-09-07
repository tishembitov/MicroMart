package ru.tishembitov.authservice.dto;

import jakarta.validation.constraints.NotNull;

public record LoginDto(
        String username,
        @NotNull
        String password
)
{
}
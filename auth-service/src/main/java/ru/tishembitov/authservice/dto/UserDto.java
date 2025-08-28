package ru.tishembitov.authservice.dto;

public record UserDto(
        Long id,
        String name,
        String lastName,
        String email,
        String username
)
{
}
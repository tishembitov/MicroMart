package ru.tishembitov.reviewsservice.dto;

public record UserDto(
        Long id,
        String name,
        String email,
        String username
)
{
}
package ru.tishembitov.userservice.dto;

import lombok.Builder;


@Builder
public record UserDto(
        Long id,
        String name,
        String email,
        String username
)
{
}
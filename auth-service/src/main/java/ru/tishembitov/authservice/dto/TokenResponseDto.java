package ru.tishembitov.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponseDto(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("expires_in")
        Long expiresIn
) {
    public static TokenResponseDto of(String accessToken, Long expiresIn) {
        return new TokenResponseDto(accessToken, "Bearer", expiresIn);
    }
}
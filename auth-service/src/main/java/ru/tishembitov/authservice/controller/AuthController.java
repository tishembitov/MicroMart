package ru.tishembitov.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tishembitov.authservice.dto.LoginDto;
import ru.tishembitov.authservice.dto.TokenResponseDto;
import ru.tishembitov.authservice.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "User login",
            description = "Authenticate user and receive access token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("Login attempt for user: {}", loginDto.username());
        TokenResponseDto token = authService.login(loginDto);
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Validate token",
            description = "Internal endpoint for token validation",
            hidden = true
    )
    @PostMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        String token = authorization.replace("Bearer ", "");
        var userHeader = authService.validateToken(token);

        return ResponseEntity.ok()
                .header("userId", userHeader.id().toString())
                .header("username", userHeader.username())
                .build();
    }
}
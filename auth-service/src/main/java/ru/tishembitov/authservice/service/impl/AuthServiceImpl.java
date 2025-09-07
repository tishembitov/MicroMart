package ru.tishembitov.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tishembitov.authservice.dto.*;
import ru.tishembitov.authservice.exception.AuthException;
import ru.tishembitov.authservice.config.JwtService;
import ru.tishembitov.authservice.service.AuthService;
import ru.tishembitov.authservice.service.UserServiceClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserServiceClient userServiceClient;

    @Override
    public TokenResponseDto login(LoginDto loginDto) {
        var user = userServiceClient.getUserForLogin(loginDto)
                .orElseThrow(() -> new AuthException(AuthException.GENERIC_LOGIN_FAIL));

        String accessToken = jwtService.generateAccessToken(user);

        return TokenResponseDto.of(
                accessToken,
                jwtService.getAccessTokenExpirationInSeconds()
        );
    }

    @Override
    public UserHeader validateToken(String jwt) {
        return jwtService.validateAndExtractUser(jwt)
                .orElseThrow(() -> new AuthException("Invalid or expired token"));
    }

}
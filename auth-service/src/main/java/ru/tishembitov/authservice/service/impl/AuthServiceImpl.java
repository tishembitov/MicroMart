package ru.tishembitov.authservice.service.impl;

import ru.tishembitov.authservice.exception.AuthException;
import ru.tishembitov.authservice.dto.LoginDto;
import ru.tishembitov.authservice.dto.UserHeader;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.tishembitov.authservice.jwt.JwtService;
import ru.tishembitov.authservice.service.AuthService;
import ru.tishembitov.authservice.service.UserServiceClient;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl
        implements AuthService {

    private final JwtService jwtService;
    private final UserServiceClient userServiceClient;

    @Override
    public String login(final LoginDto loginDto) {

        var user = this.userServiceClient.getUserForLogin(loginDto)
                                         .orElseThrow(() -> new AuthException(AuthException.GENERIC_LOGIN_FAIL));

        try {
            return this.jwtService.generateToken(user);
        } catch (JsonProcessingException e) {
            throw new AuthException(AuthException.GENERIC_LOGIN_FAIL);
        }
    }


    @Override
    public UserHeader validateToken(final String jwt) {
        return this.jwtService.extractUsername(jwt)
                             .orElseThrow(() -> new AuthException(
                                     HttpStatus.UNAUTHORIZED,
                                     AuthException.GENERIC_LOGIN_FAIL
                             ));
    }
}

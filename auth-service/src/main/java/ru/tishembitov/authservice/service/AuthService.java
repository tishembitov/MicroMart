package ru.tishembitov.authservice.service;

import ru.tishembitov.authservice.dto.LoginDto;
import ru.tishembitov.authservice.dto.TokenResponseDto;
import ru.tishembitov.authservice.dto.UserHeader;

public interface AuthService {
    TokenResponseDto login(LoginDto loginDto);
    UserHeader validateToken(String jwt);
}
package ru.tishembitov.authservice.service;

import ru.tishembitov.authservice.dto.LoginDto;
import ru.tishembitov.authservice.dto.UserDto;

import java.util.Optional;

public interface UserServiceClient {
    Optional<UserDto> getUserForLogin(LoginDto loginDto);
    Optional<UserDto> getUserById(Long id);
}

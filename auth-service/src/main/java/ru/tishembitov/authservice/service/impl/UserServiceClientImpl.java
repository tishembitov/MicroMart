package ru.tishembitov.authservice.service.impl;

import ru.tishembitov.authservice.exception.AuthException;
import ru.tishembitov.authservice.dto.LoginDto;
import ru.tishembitov.authservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.tishembitov.authservice.service.UserServiceClient;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceClientImpl implements UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${api.user-service}")
    private String userServiceUrl;

    @Override
    public Optional<UserDto> getUserForLogin(final LoginDto loginDto) {
        try {
            var url = this.userServiceUrl + "/login";

            var response = this.restTemplate.postForEntity(url, loginDto, UserDto.class);

            if (response.getStatusCode().isError()) {
                throw new AuthException(AuthException.GENERIC_LOGIN_FAIL);
            }

            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            log.error("Error getting user for login: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        try {
            var url = this.userServiceUrl + "/users/" + id;

            var response = this.restTemplate.getForEntity(url, UserDto.class);

            if (response.getStatusCode().isError()) {
                log.error("Error response when getting user by id {}: {}", id, response.getStatusCode());
                return Optional.empty();
            }

            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            log.error("Error getting user by id {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
}

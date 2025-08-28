package ru.tishembitov.userservice.service;

import ru.tishembitov.userservice.dto.LoginDto;
import ru.tishembitov.userservice.dto.UserCreateDto;
import ru.tishembitov.userservice.dto.UserDto;
import ru.tishembitov.userservice.dto.UserEditDto;

public interface UserService {
    UserDto addOne(UserCreateDto userCreateDto);

    UserDto getUser();

    UserDto editUser(UserEditDto userEditDto);

    UserDto getUserLogin(LoginDto loginDto);
}



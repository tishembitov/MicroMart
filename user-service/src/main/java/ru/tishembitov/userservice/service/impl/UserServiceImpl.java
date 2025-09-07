package ru.tishembitov.userservice.service.impl;

import org.springframework.transaction.annotation.Transactional;
import ru.tishembitov.userservice.config.ContextHolder;
import ru.tishembitov.userservice.exception.UserException;
import ru.tishembitov.userservice.dto.LoginDto;
import ru.tishembitov.userservice.dto.UserCreateDto;
import ru.tishembitov.userservice.dto.UserDto;
import ru.tishembitov.userservice.dto.UserEditDto;
import ru.tishembitov.userservice.mapper.UserMapper;
import ru.tishembitov.userservice.repository.UserRepository;
import ru.tishembitov.userservice.service.UserService;
import ru.tishembitov.userservice.util.BCryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ContextHolder contextHolder;

    @Override
    @Transactional
    public UserDto addOne(final UserCreateDto userCreateDto) {

        var hashedPassword = BCryptUtil.hashPassword(userCreateDto.password());

        var user = this.userMapper.toUser(userCreateDto, hashedPassword);

        var exists = this.userRepository.existsUser(user);

        if (exists) {
            throw new UserException(UserException.USER_ALREADY_EXISTS);
        }

        var saveSaved = this.userRepository.save(user);

        return this.userMapper.toDto(saveSaved);
    }

    @Override
    public UserDto getUser() {
        var userSaved = this.userRepository.findByUsername(this.contextHolder.getUsername())
                .orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));

        return this.userMapper.toDto(userSaved);
    }

    @Override
    public UserDto getUserLogin(LoginDto loginDto) {
        var user = this.userRepository.findByUsername(loginDto.username())
                .orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));

        if (!BCryptUtil.checkPassword(loginDto.password(), user.getPassword())) {
            throw new UserException(UserException.USER_NOT_FOUND);
        }

        return this.userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto editUser(final UserEditDto userEditDto) {

        var user = this.userRepository.findById(userEditDto.id())
                .orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));

        this.userMapper.partialUpdate(userEditDto, user);

        var userEdited = this.userRepository.save(user);

        return this.userMapper.toDto(userEdited);
    }
}
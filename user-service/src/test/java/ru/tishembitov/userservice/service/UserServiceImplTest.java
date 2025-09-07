package ru.tishembitov.userservice.service;

import ru.tishembitov.userservice.config.ContextHolder;
import ru.tishembitov.userservice.exception.UserException;
import ru.tishembitov.userservice.dto.LoginDto;
import ru.tishembitov.userservice.dto.UserCreateDto;
import ru.tishembitov.userservice.dto.UserDto;
import ru.tishembitov.userservice.dto.UserEditDto;
import ru.tishembitov.userservice.entity.User;
import ru.tishembitov.userservice.mapper.UserMapper;
import ru.tishembitov.userservice.repository.UserRepository;
import ru.tishembitov.userservice.service.impl.UserServiceImpl;
import ru.tishembitov.userservice.util.BCryptUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ContextHolder contextHolder;
    @InjectMocks
    UserServiceImpl userServiceImpl;
    @Mock
    private UserMapper userMapper;

    private UserCreateDto userCreateDto;
    private User user;
    private UserDto userDto;
    private LoginDto loginDto;
    private UserEditDto userEditDto;
    private String hashedPassword;
    private String rawPassword;

    @BeforeEach
    void beforeEach() {
        this.rawPassword = "pwd";
        this.hashedPassword = "$2a$10$dummyHashedPassword"; // Примерный BCrypt хеш

        this.userCreateDto = UserCreateDto.builder()
                .name("John")
                .email("jhondoe@gmail.com")
                .username("jhondoe")
                .password(rawPassword)
                .build();

        this.user = User.builder()
                .email(this.userCreateDto.email())
                .name(this.userCreateDto.name())
                .password(hashedPassword)
                .username(this.userCreateDto.username())
                .build();

        this.userDto = UserDto.builder()
                .email(this.user.getEmail())
                .name(this.user.getName())
                .username(this.user.getUsername())
                .build();

        this.loginDto = LoginDto.builder()
                .username(this.user.getUsername())
                .password(rawPassword) // Используем сырой пароль
                .build();

        this.userEditDto = UserEditDto.builder()
                .id(1L)
                .name("newName")
                .password("pwd")
                .build();

        lenient().when(this.contextHolder.getUserId())
                .thenReturn(1L);
        lenient().when(this.contextHolder.getUsername())
                .thenReturn(this.userCreateDto.username());
    }

    @Test
    void addOne_WhenUserDoesNotExist_ShouldSaveUser() {
        //Arrange
        try (MockedStatic<BCryptUtil> mockedBCrypt = mockStatic(BCryptUtil.class)) {
            mockedBCrypt.when(() -> BCryptUtil.hashPassword(rawPassword))
                    .thenReturn(hashedPassword);

            when(this.userMapper.toUser(this.userCreateDto, hashedPassword))
                    .thenReturn(this.user);
            when(this.userRepository.existsUser(this.user))
                    .thenReturn(false);
            when(this.userRepository.save(this.user))
                    .thenReturn(this.user);
            when(this.userMapper.toDto(this.user))
                    .thenReturn(this.userDto);

            //Act
            var addedUser = this.userServiceImpl.addOne(this.userCreateDto);

            //Assert
            Assertions.assertEquals(this.userDto, addedUser);
        }
    }

    @Test
    void addOne_WhenUserAlreadyExists_ShouldThrowUserException() {
        // Arrange
        try (MockedStatic<BCryptUtil> mockedBCrypt = mockStatic(BCryptUtil.class)) {
            mockedBCrypt.when(() -> BCryptUtil.hashPassword(rawPassword))
                    .thenReturn(hashedPassword);

            when(this.userMapper.toUser(this.userCreateDto, hashedPassword))
                    .thenReturn(this.user);
            when(this.userRepository.existsUser(this.user))
                    .thenReturn(true);

            // Act and Assert
            Assertions.assertThrows(
                    UserException.class,
                    () -> this.userServiceImpl.addOne(this.userCreateDto),
                    UserException.USER_ALREADY_EXISTS
            );

            verify(this.userRepository, never()).save(this.user);
        }
    }

    @Test
    void getUser_WhenUserDoesNotExist_ShouldThrowUserException() {
        // Arrange
        when(this.userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        // Act and Assert
        Assertions.assertThrows(
                UserException.class,
                this.userServiceImpl::getUser,
                UserException.USER_ALREADY_EXISTS
        );
    }

    @Test
    void getUser_WhenUserExists_ShouldReturnsUser() {
        // Arrange
        when(this.userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(this.user));
        when(this.userMapper.toDto(any(User.class)))
                .thenReturn(this.userDto);

        //Act
        var userGet = this.userServiceImpl.getUser();

        //Assert
        Assertions.assertEquals(this.userDto, userGet);
    }

    @Test
    void getUserLogin_WhenValidLogin_ShouldReturnsUser() {
        //Arrange
        try (MockedStatic<BCryptUtil> mockedBCrypt = mockStatic(BCryptUtil.class)) {
            when(this.userRepository.findByUsername(loginDto.username()))
                    .thenReturn(Optional.of(this.user));
            mockedBCrypt.when(() -> BCryptUtil.checkPassword(rawPassword, hashedPassword))
                    .thenReturn(true);
            when(this.userMapper.toDto(any(User.class)))
                    .thenReturn(this.userDto);

            // Act
            var userGet = this.userServiceImpl.getUserLogin(this.loginDto);

            // Assert
            Assertions.assertEquals(this.userDto, userGet);
        }
    }

    @Test
    void getUserLogin_WhenInvalidPassword_ShouldThrowUserException() {
        //Arrange
        try (MockedStatic<BCryptUtil> mockedBCrypt = mockStatic(BCryptUtil.class)) {
            when(this.userRepository.findByUsername(loginDto.username()))
                    .thenReturn(Optional.of(this.user));
            mockedBCrypt.when(() -> BCryptUtil.checkPassword(rawPassword, hashedPassword))
                    .thenReturn(false);

            // Act and Assert
            Assertions.assertThrows(
                    UserException.class,
                    () -> this.userServiceImpl.getUserLogin(this.loginDto),
                    UserException.USER_NOT_FOUND
            );
        }
    }

    @Test
    void getUserLogin_WhenUserNotFound_ShouldThrowUserException() {
        //Arrange
        when(this.userRepository.findByUsername(loginDto.username()))
                .thenReturn(Optional.empty());

        // Act and Assert
        Assertions.assertThrows(
                UserException.class,
                () -> this.userServiceImpl.getUserLogin(this.loginDto),
                UserException.USER_NOT_FOUND
        );
    }

    @Test
    void editUser_WhenValidUserEdit_ShouldReturnsEditedUser() {
        //Arrange
        when(this.userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        when(this.userRepository.save(any(User.class)))
                .thenReturn(new User());
        doNothing().when(this.userMapper)
                .partialUpdate(isA(UserEditDto.class), isA(User.class));
        when(this.userMapper.toDto(any(User.class)))
                .thenReturn(this.userDto);

        // Act
        var editedUser = this.userServiceImpl.editUser(this.userEditDto);

        // Assert
        Assertions.assertEquals(this.userDto, editedUser);
    }

    @Test
    void editUser_WhenInvalidUserEdit_ShouldThrowsUserException() {
        //Arrange
        when(this.userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // Act and Assert
        Assertions.assertThrows(
                UserException.class,
                () -> this.userServiceImpl.editUser(this.userEditDto),
                UserException.USER_NOT_FOUND
        );
    }
}
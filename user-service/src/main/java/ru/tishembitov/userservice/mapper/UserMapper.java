package ru.tishembitov.userservice.mapper;

import ru.tishembitov.userservice.dto.UserCreateDto;
import ru.tishembitov.userservice.dto.UserDto;
import ru.tishembitov.userservice.dto.UserEditDto;
import ru.tishembitov.userservice.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toUser(UserDto userDto);

    @Mapping(target = "password",
             expression = "java(password)")
    User toUser(
            UserCreateDto userCreateDto,
            @Context
            String password
    );

    User toUser(UserEditDto userEditDto);

    UserDto toDto(User user);

    void partialUpdate(
            UserDto userDto,
            @MappingTarget
            User user
    );

    void partialUpdate(
            UserCreateDto userCreateDto,
            @MappingTarget
            User user
    );

    void partialUpdate(
            UserEditDto userEditDto,
            @MappingTarget
            User user
    );
}



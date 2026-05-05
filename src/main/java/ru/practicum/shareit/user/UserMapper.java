package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    User toEntity(NewUserRequest request);

    @Mapping(target = "id", source = "userId")
    UserDto toDto(User user);

    @Mapping(target = "userId", ignore = true)
    void updateUserFromRequest(@MappingTarget User user, UpdateUserRequest request);
}

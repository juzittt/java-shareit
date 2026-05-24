package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    User toUserEntity(NewUserRequest request);

    @Mapping(target = "id", source = "userId")
    UserDto toUserDto(User user);

    @Mapping(target = "userId", ignore = true)
    void updateUserFromRequest(@MappingTarget User user, UpdateUserRequest request);
}

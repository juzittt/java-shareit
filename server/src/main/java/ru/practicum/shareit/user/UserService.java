package ru.practicum.shareit.user;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createUser(NewUserRequest request);

    UserDto updateUser(Long userId, UpdateUserRequest request);

    UserDto getUser(Long userId);

    Page<UserDto> getUsers(Long userId, Integer from, Integer size);

    void deleteUser(Long userId);
}

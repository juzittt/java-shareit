package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

public interface UserRepository {
    UserDto createUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(Long userId);

    void deleteUserById(Long userId);

    Boolean isEmailExists(String email);
}

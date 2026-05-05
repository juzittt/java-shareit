package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUser(long id) {
        return userRepository.getUserById(id)
                .orElseThrow(() -> {
                    log.warn("User with id {} doesn`t exists", id);
                    return new NotFoundException("Пользователь с id " + id + " не найден.");
                });
    }

    public UserDto createUser(@Valid NewUserRequest request) {
        validateEmail(request.getEmail());

        User user = userMapper.toEntity(request);
        return userRepository.createUser(user);
    }

    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        User updateUser = getUser(userId);

        request.setId(userId);

        updateIfNotBlank(request, updateUser);
        userMapper.updateUserFromRequest(updateUser, request);
        userRepository.updateUser(updateUser);
        return userMapper.toDto(updateUser);
    }

    public UserDto getUserById(Long userId) {
        User user = getUser(userId);
        return userMapper.toDto(user);
    }

    public void deleteUser(Long userId) {
        getUser(userId);
        userRepository.deleteUserById(userId);
    }

    private void validateEmail(String email) {
        if (userRepository.isEmailExists(email)) {
            log.warn("Email {} already exists", email);
           throw new DuplicatedDataException("Email: " + email + " уже существует.");
        }
    }


    private void updateIfNotBlank(UpdateUserRequest request, User user) {
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        String newEmail = request.getEmail();
        if (newEmail != null && !newEmail.isBlank()) {
            if (!newEmail.equals(user.getEmail()) && userRepository.isEmailExists(newEmail)) {
                throw new DuplicatedDataException("Email: " + newEmail + " уже существует.");
            }
            user.setEmail(newEmail);
        }
    }
}

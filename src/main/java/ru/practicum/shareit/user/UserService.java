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
        log.debug("[GetUser] Getting user with id: {}", id);
        return userRepository.getUserById(id)
                .orElseThrow(() -> {
                    log.warn("[GetUser - Error] User with id {} doesn`t exists", id);
                    return new NotFoundException("Пользователь с id " + id + " не найден.");
                });
    }

    public UserDto createUser(@Valid NewUserRequest request) {
        log.debug("[CreateUser] Create new user with name: {} and email: {}", request.getName(), request.getEmail());
        validateEmail(request.getEmail());

        User user = userMapper.toEntity(request);
        return userRepository.createUser(user);
    }

    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        log.debug("[UpdateUser] Update for user with id: {}. Fields from request: name: {}, email: {}",
                userId, request.getName(), request.getEmail());
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
        log.debug("[DeleteUser] delete user with id: {}", userId);
        getUser(userId);
        userRepository.deleteUserById(userId);
    }

    private void validateEmail(String email) {
        log.debug("[ValidateEmail] Validate email: {}", email);
        if (userRepository.isEmailExists(email)) {
            log.warn("[ValidateEmail - Error] Email {} already exists", email);
           throw new DuplicatedDataException("Email: " + email + " уже существует.");
        }
    }


    private void updateIfNotBlank(UpdateUserRequest request, User user) {
        log.debug("[UpdateFieldsIfNotBlanks] Updating fields");
        if (request.getName() != null && !request.getName().isBlank()) {
            log.debug("[UpdateFieldsIfNotBlanks - Name] Update name from '{}' to '{}'", user.getName(), request.getName());
            user.setName(request.getName());
        }

        String newEmail = request.getEmail();
        if (newEmail != null && !newEmail.isBlank()) {
            log.debug("[UpdateFieldsIfNotBlanks - Email] Update email from '{}' to '{}'", user.getEmail(), newEmail);
            if (!newEmail.equals(user.getEmail()) && userRepository.isEmailExists(newEmail)) {
                log.warn("[UpdateFieldsIfNotBlanks - Email - Error] Email is already exists");
                throw new DuplicatedDataException("Email: " + newEmail + " уже существует.");
            }
            user.setEmail(newEmail);
        }
    }
}

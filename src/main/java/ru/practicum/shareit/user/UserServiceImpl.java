package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser(Long id) {
        log.debug("[GetUser] Getting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[GetUser - Error] User with id {} doesn`t exists", id);
                    return new NotFoundException("Пользователь с Id " + id + ". Не найден");
                });

        return userMapper.toUserDto(user);

    }

    public UserDto createUser(@Valid NewUserRequest request) {
        log.debug("[CreateUser] Create new user with name: {} and email: {}", request.getName(), request.getEmail());
        User user = userMapper.toUserEntity(request);
        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        log.debug("[UpdateUser] Update for user with id: {}. Fields from request: name: {}, email: {}",
                userId, request.getName(), request.getEmail());
        User user = checkUserExists(userId);
        userMapper.updateUserFromRequest(user, request);
        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("[DeleteUser] delete user with id: {}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    private User checkUserExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[CheckUserExists] User {} not found", userId);
                    return new NotFoundException("Пользователь с Id " + userId + " not found");
                });
    }
}

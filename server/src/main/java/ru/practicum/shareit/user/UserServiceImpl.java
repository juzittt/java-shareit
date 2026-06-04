package ru.practicum.shareit.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Data
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser(Long id) {
        log.debug("[GetUser] Getting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[GetUser - Error] User with id {} doesn`t exists", id);
                    return new NotFoundException("Пользователь с Id " + id + " не найден");
                });

        return userMapper.toUserDto(user);

    }

    @Transactional
    public UserDto createUser(NewUserRequest request) {
        log.debug("[CreateUser] Create new user with name: {} and email: {}", request.getName(), request.getEmail());
        User user = userMapper.toUserEntity(request);
        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        log.debug("[UpdateUser] Update for user with id: {}. Fields from request: name: {}, email: {}",
                userId, request.getName(), request.getEmail());
        User user = checkUserExists(userId);
        userMapper.updateUserFromRequest(user, request);
        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("[DeleteUser] delete user with id: {}", userId);
        checkUserExists(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public Page<UserDto> getUsers(Long userId, Integer from, Integer size) {
        log.info("GET users");
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").descending());

        Page<UserDto> users = userRepository.findAll(pageable)
                .map(userMapper::toUserDto);

        log.info("FIND users: size={}", users.getTotalElements());

        return  users;
    }

    private User checkUserExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[CheckUserExists] User {} not found", userId);
                    return new NotFoundException("Пользователь с Id " + userId + " не найден");
                });
    }
}

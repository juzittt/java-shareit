package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(User user) {
        long id = getNextId();

        user.setUserId(id);
        users.put(id, user);

        return userMapper.toDto(user);
    }

    @Override
    public User updateUser(User user) {
        return users.put(user.getUserId(), user);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void deleteUserById(Long userId) {
        users.remove(userId);
    }

    @Override
    public Boolean isEmailExists(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestMapper requestMapper;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemRequestDto addRequest(Long userId, NewItemRequest request) {
        User requester = validateUser(userId);
        ItemRequest itemRequest = requestMapper.toItemEntity(request);

        itemRequest.setRequestor(requester);

        ItemRequest savedRequest = requestRepository.save(itemRequest);
        return requestMapper.toRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        validateUser(userId);

        List<ItemRequestDto> requests = requestRepository.findAllByRequestor_UserIdOrderByCreatedDesc(userId)
                .stream()
                .map(requestMapper::toRequestDto)
                .toList();
        return requests;
    }

    @Override
    public ItemRequestDto getRequest(Long userId, Long requestId) {
        validateUser(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с Id: " + requestId + ". Не существует"));

        return requestMapper.toRequestDto(request);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        validateUser(userId);
        List<ItemRequestDto> requests = requestRepository.findAll(Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(requestMapper::toRequestDto)
                .toList();
        return requests;
    }

    private User validateUser(Long userId) {
        log.debug("[ValidateUser] Validating user id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[ValidateUser] User with id: {} not found", userId);
                    return new NotFoundException("Пользователь с Id " + userId + ", не найден");
                });
    }
}

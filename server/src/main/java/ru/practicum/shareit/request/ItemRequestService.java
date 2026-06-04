package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestReq;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addRequest(Long userId, NewItemRequestReq request);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestDto getRequest(Long userId, Long requestId);
}

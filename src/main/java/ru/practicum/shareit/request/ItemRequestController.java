package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @Valid @RequestBody NewItemRequest request){
        ItemRequestDto addedItemRequest = requestService.addRequest(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItemRequest);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId){
        return ResponseEntity.ok(requestService.getUserRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getRequestAll(@RequestHeader("X-Sharer-User-Id") Long userId){
        return ResponseEntity.ok(requestService.getAllRequests(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable("requestId") Long requestId){
        return ResponseEntity.ok(requestService.getRequest(userId, requestId));
    }
}

package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody @Valid ItemDto item) {
        ItemDto addedItem = itemService.addItem(userId, item);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody @Valid UpdateItemRequest request) {
        return ResponseEntity.ok(itemService.updateItem(userId, itemId, request));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemById(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItemByName(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam String text) {
        return ResponseEntity.ok(itemService.searchItem(userId, text));
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        log.debug("[AddItem] Adding a new item '{}', by user with id: {}", itemDto.getName(), userId);
        Item item = itemMapper.toEntity(itemDto);
        log.debug("[AddItem] Setting user with id: {}, for item '{}'", userId, itemDto.getName());
        item.setOwner(userService.getUser(userId));
        itemRepository.addItem(item);
        return getItemById(item.getItemId());
    }

    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request) {
        log.debug("[UpdateItem] Updating item with id: {}", itemId);
        Item item = validateOwner(userId, itemId);
        itemMapper.updateItemFromRequest(item, request);
        Item updatedItem = itemRepository.updateItem(item);
        return getItemById(updatedItem.getItemId());
    }

    public List<ItemDto> getItems(Long userId) {
        log.debug("[GetItems] Getting all user items");
        return itemRepository.getItems(userId);
    }

    public ItemDto getItemById(Long id) {
        log.debug("[GetItemById] Getting item with id: {}", id);
        return itemMapper.toDto(getItemEntity(id));
    }

    public Item getItemEntity(Long id) {
        return itemRepository.getItem(id)
                .orElseThrow(() -> {
                    log.warn("[getItemEntity - Error] Item with id:{} not found", id);
                    return new NotFoundException("Предмет с id = " + id + " не найден.");
                });
    }

    public List<ItemDto> searchItem(Long userId, String text) {
        log.debug("[SearchItem] Searching items whose owner`s id: {}, contains text: '{}'", userId, text);
        if (text == null || text.isEmpty()) {
            log.debug("[SearchItem] Text to find in name or description was empty");
            return List.of();
        }
        return itemMapper.toDtoList(itemRepository.searchItem(userId, text));
    }

    private Item validateOwner(Long userId, Long itemId) {
        log.debug("[ValidateOwner] Validating owner id: {} for item with id: {}", userId, itemId);
        User user = userService.getUser(userId);
        Item entity = itemRepository.getItem(itemId)
                .filter(item -> item.getOwner() != null
                        && item.getOwner().getUserId().equals(user.getUserId()))
                .orElseThrow(() -> {
                    log.warn("[validateOwner - Error] Item with id: {} which owner id is {} doesn`t exist", itemId, userId);
                    return new NotFoundException("Предмет с id = " + itemId +
                            ", где владелец c id=" + userId + " не найден.");
                });
        return entity;
    }
}

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
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(userService.getUser(userId));
        itemRepository.addItem(item);
        return getItemById(item.getItemId());
    }

    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request) {
        log.info("Updating item with id={}", itemId);
        Item item = validateOwner(userId, itemId);
        itemMapper.updateItemFromRequest(item, request);
        Item updatedItem = itemRepository.updateItem(item);
        return getItemById(updatedItem.getItemId());
    }

    public List<ItemDto> getItems(Long userId) {
        return itemRepository.getItems(userId);
    }

    public ItemDto getItemById(Long id) {
        return itemMapper.toDto(getItemEntity(id));
    }

    public Item getItemEntity(Long id) {
        return itemRepository.getItem(id)
                .orElseThrow(() -> {
                    log.warn("Item with id={} not found", id);
                    return new NotFoundException("Предмет с id = " + id + " не найден.");
                });
    }

    public List<ItemDto> searchItem(Long userId, String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemMapper.toDtoList(itemRepository.searchItem(userId, text));
    }

    private Item validateOwner(Long userId, Long itemId) {
        User user = userService.getUser(userId);
        Item entity = itemRepository.getItem(itemId)
                .filter(item -> item.getOwner() != null
                        && item.getOwner().getUserId().equals(user.getUserId()))
                .orElseThrow(() -> {
                    log.warn("Item with id={} which owner id is {} doesn`t exist", itemId, userId);
                    return new NotFoundException("Предмет с id = " + itemId +
                            ", где владелец c id=" + userId + " не найден.");
                });
        return entity;
    }
}

package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getItem(Long id);

    List<Item> getAllItems();

    List<ItemDto> getItems(Long userId);

    List<Item> getItemsByOwnerId(Long ownerId);

    List<Item> searchItem(Long userId, String text);
}

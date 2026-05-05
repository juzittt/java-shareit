package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    private final ItemMapper itemMapper;

    @Override
    public Item addItem(Item item) {
        long id = getNextId();

        item.setItemId(id);
        items.put(id, item);

        return item;
    }

    @Override
    public Item updateItem(Item item) {
        return items.put(item.getItemId(), item);
    }

    @Override
    public Optional<Item> getItem(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return userId == null ? itemMapper.toDtoList(getAllItems())
                : itemMapper.toDtoList(getItemsByOwnerId(userId));
    }

    @Override
    public List<Item> getItemsByOwnerId(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getUserId().equals(ownerId))
                .toList();
    }

    @Override
    public List<Item> searchItem(Long userId, String text) {
        return getItemsByOwnerId(userId).stream()
                .filter(item -> item.getName().equalsIgnoreCase(text))
                .filter(Item::getIsAvailable)
                .toList();
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

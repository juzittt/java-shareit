package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long itemId;
    private String name;
    private String description;
    private Boolean isAvailable;
    private User owner;
    private ItemRequest request;
}

package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, NewItemRequest request);

    ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request);

    void deleteItem(Long itemId, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemBooking> getItems(Long userId);

    ItemBooking getItem(Long userId, Long itemId);

    List<ItemDto> searchItems(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, NewCommentRequest request);
}

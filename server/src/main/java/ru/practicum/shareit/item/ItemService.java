package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

public interface ItemService {

    ItemDto addItem(Long userId, NewItemRequest request);

    ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request);

    void deleteItem(Long itemId, Long userId);

    Page<ItemBooking> getItems(Long userId, Integer from, Integer size);

    ItemBooking getItem(Long userId, Long itemId);

    Page<ItemDto> searchItems(Long userId, String text, Integer from, Integer size);

    CommentDto addComment(Long userId, Long itemId, NewCommentRequest request);
}

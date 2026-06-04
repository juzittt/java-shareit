package ru.practicum.shareit.item.comment.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private ItemDto item;
    private String authorName;
    private LocalDateTime created;
}

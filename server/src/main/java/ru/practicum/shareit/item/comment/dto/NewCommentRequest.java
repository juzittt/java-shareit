package ru.practicum.shareit.item.comment.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class NewCommentRequest {
    private String text;
    private LocalDateTime createdAt = LocalDateTime.now();
}

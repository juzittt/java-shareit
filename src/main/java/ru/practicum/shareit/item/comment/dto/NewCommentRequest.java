package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewCommentRequest {

    @NotNull(message = "Текст не может быть null")
    @NotBlank(message = "Текст не может быть пустым")
    private String text;

    private LocalDateTime created = LocalDateTime.now();
}

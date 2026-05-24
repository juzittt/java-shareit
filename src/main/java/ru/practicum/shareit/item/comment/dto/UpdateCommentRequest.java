package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCommentRequest {

    @NotNull(message = "Текст не может быть null")
    @NotBlank(message = "Текст не может быть пустым")
    private String text;
}

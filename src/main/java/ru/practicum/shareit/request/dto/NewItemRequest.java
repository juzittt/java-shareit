package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewItemRequest {
    @NotNull(message = "Описание не может быть пустым")
    private String description;

    private LocalDateTime created = LocalDateTime.now();
}

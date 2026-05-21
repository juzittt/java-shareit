package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateItemRequest {
    @NotNull(message = "Id не может быть пустым")
    private Long id;

    private String description;
}

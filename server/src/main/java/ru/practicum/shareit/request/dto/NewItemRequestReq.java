package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewItemRequestReq {
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}

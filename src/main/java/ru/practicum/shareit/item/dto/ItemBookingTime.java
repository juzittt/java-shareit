package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemBookingTime {
    LocalDateTime start;
    LocalDateTime end;
}

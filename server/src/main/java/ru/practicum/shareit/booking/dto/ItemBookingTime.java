package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemBookingTime {
    LocalDateTime start;
    LocalDateTime end;
}

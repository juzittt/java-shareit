package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
}

package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
public class UpdateBookingRequest {
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
}

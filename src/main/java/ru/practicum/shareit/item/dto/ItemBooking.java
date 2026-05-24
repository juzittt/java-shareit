package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Data
public class ItemBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemBookingTime lastBooking;
    private ItemBookingTime nextBooking;
    private List<CommentDto> comments;
}

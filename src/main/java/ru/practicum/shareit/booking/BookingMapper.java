package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemBookingTime;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "endDate", source = "end")
    @Mapping(target = "startDate", source = "start")
    Booking toBookingEntity(NewBookingRequest request);

    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "end", source = "endDate")
    @Mapping(target = "start", source = "startDate")
    BookingDto toBookingDto(Booking booking);

    ItemBookingTime toItemBookingsTimes(Booking booking);
}

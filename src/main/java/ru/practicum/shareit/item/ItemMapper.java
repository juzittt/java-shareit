package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

    @Mapping(target = "id", source = "itemId")
    ItemDto toItemDto(Item item);

    @Mapping(target = "itemId", ignore = true)
    void updateItemFromRequest(@MappingTarget Item item, UpdateItemRequest request);

    @Mapping(target = "id", source = "itemId")
    List<ItemDto> toItemDtoList(List<Item> items);

    @Mapping(target = "itemId", source = "id")
    Item toItemEntity(ItemDto dto);

    @Mapping(target = "itemId", ignore = true)
    Item newItemRequest(NewItemRequest request);

    @Mapping(target = "id", source = "itemId")
    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ItemBooking toItemBooking(Item item);
}

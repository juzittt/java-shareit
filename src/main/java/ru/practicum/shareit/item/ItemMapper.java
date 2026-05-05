package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", source = "itemId")
    ItemDto toDto(Item item);

    @Mapping(target = "itemId", ignore = true)
    void updateItemFromRequest(@MappingTarget Item item, UpdateItemRequest request);

    @Mapping(target = "id", source = "itemId")
    List<ItemDto> toDtoList(List<Item> items);

    @Mapping(target = "itemId", source = "id")
    Item toEntity(ItemDto dto);
}

package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestReq;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestor", ignore = true)
    @Mapping(target = "items", ignore = true)
    ItemRequest toItemEntity(NewItemRequestReq request);

    ItemRequestDto toRequestDto(ItemRequest request);
}

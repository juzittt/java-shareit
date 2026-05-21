package ru.practicum.shareit.item.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.comment.model.Comment;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commentator", ignore = true)
    @Mapping(target = "item", ignore = true)
    Comment toCommentEntity(NewCommentRequest request);

    @Mapping(target = "authorName", source = "commentator.name")
    CommentDto toCommentDto(Comment comment);
}

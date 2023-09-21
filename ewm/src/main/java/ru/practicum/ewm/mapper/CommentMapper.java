package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.model.Comment;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {
    Comment toEntity(NewCommentDto commentDto);

    Comment toEntity(UpdateCommentDto commentDto);

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "author", source = "author.id")
    CommentDto toDto(Comment comment);
}

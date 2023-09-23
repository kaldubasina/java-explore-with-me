package ru.practicum.ewm.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.service.comment.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@Validated
public class PrivateCommentController {
    private final CommentService service;
    private final CommentMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto add(@PathVariable @Positive Long userId,
                          @PathVariable @Positive Long eventId,
                          @RequestBody @Valid NewCommentDto commentDto) {
        return mapper.toDto(service.add(userId, eventId, mapper.toEntity(commentDto)));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long userId,
                       @PathVariable @Positive Long eventId,
                       @PathVariable @Positive Long commentId) {
        service.delete(userId, eventId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable @Positive Long userId,
                             @PathVariable @Positive Long eventId,
                             @PathVariable @Positive Long commentId,
                             @RequestBody @Valid UpdateCommentDto commentDto) {
        return mapper.toDto(service.update(userId, eventId, commentId, mapper.toEntity(commentDto)));
    }
}

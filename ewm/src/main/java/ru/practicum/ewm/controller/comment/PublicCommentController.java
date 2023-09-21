package ru.practicum.ewm.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.service.comment.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
@Validated
public class PublicCommentController {
    private final CommentService service;
    private final CommentMapper mapper;

    @GetMapping
    public List<CommentDto> getByEvent(@PathVariable @Positive Long eventId,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(defaultValue = "10") @Positive Integer size) {
        return service.getByEvent(eventId, from, size).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{commentId}")
    public CommentDto getById(@PathVariable @Positive Long eventId,
                              @PathVariable @Positive Long commentId) {
        return mapper.toDto(service.getById(eventId, commentId));
    }
}

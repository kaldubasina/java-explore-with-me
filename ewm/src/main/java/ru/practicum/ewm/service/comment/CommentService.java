package ru.practicum.ewm.service.comment;

import ru.practicum.ewm.model.Comment;

import java.util.List;

public interface CommentService {
    Comment add(Long userId, Long eventId, Comment comment);

    void delete(Long userId, Long eventId, Long commentId);

    Comment update(Long userId, Long eventId, Long commentId, Comment comment);

    List<Comment> getByEvent(Long eventId, Integer from, Integer size);

    Comment getById(Long eventId, Long commentId);

    void deleteByAdmin(Long commentId);
}

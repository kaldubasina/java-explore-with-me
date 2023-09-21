package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndEventIdAndAuthorId(Long commentId, Long eventId, Long userId);

    boolean existsByIdAndEventIdAndAuthorId(Long commentId, Long eventId, Long userId);

    List<Comment> findByEventId(Long eventId, Pageable page);

    Optional<Comment> findByIdAndEventId(Long commentId, Long eventId);
}

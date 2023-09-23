package ru.practicum.ewm.service.comment;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.State;
import ru.practicum.ewm.model.enums.Status;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository,
                              RequestRepository requestRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException(String.format("Comment with id %d not found", commentId));
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public Comment add(Long userId, Long eventId, Comment comment) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id %d not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotAvailableException("Cannot comment in an unpublished event");
        }
        if (!requestRepository.existsByEventIdAndRequesterIdAndStatus(eventId, userId, Status.CONFIRMED) &&
        event.getInitiator().getId() != user.getId()) {
            throw new NotAvailableException("Only event participants and initiator can leave comments");
        }
        comment.setAuthor(user);
        comment.setEvent(event);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long eventId, Long commentId) {
        if (!commentRepository.existsByIdAndEventIdAndAuthorId(commentId, eventId, userId)) {
            throw new NotFoundException(String.format("Comment with id %d not found", commentId));
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public Comment update(Long userId, Long eventId, Long commentId, Comment comment) {
        Comment commForUpd = commentRepository.findByIdAndEventIdAndAuthorId(commentId, eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Comment with id %d not found", commentId)));
        commForUpd.setText(comment.getText());
        return commentRepository.save(commForUpd);
    }

    @Override
    public List<Comment> getByEvent(Long eventId, Integer from, Integer size) {
        return commentRepository.findByEventId(eventId, PageRequest.of(from / size, size));
    }

    @Override
    public Comment getById(Long eventId, Long commentId) {
        return commentRepository.findByIdAndEventId(commentId, eventId).orElseThrow(() ->
                new NotFoundException(String.format("Comment with id %d not found", commentId)));
    }
}

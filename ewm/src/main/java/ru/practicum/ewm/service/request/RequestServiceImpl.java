package ru.practicum.ewm.service.request;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.State;
import ru.practicum.ewm.model.enums.Status;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    public RequestServiceImpl(UserRepository userRepository,
                              EventRepository eventRepository,
                              RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional
    public Request add(long userId, long eventId) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id %d not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new NotAvailableException("Request has already been added");
        }
        if (event.getInitiator().getId() == requester.getId()) {
            throw new NotAvailableException("Initiator can't participate in an event");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotAvailableException("Cannot participate in an unpublished event");
        }
        if (event.getParticipantLimit() == requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED)) {
            throw new NotAvailableException("Request limit reached");
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .build();

        request.setStatus(!event.getRequestModeration() || event.getParticipantLimit() == 0 ?
                Status.CONFIRMED : Status.PENDING);

        return requestRepository.save(request);
    }

    @Override
    public List<Request> getRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
        return requestRepository.findByRequesterId(userId);
    }

    @Override
    @Transactional
    public Request cancelRequest(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Request with id %d not found", requestId)));
        if (request.getRequester().getId() != userId) {
            throw new NotFoundException("The request was left by another user");
        }
        request.setStatus(Status.CANCELED);
        return requestRepository.save(request);
    }
}

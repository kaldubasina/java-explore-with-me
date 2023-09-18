package ru.practicum.ewm.service.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.exception.IncorrectRangeException;
import ru.practicum.ewm.exception.NotAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventSort;
import ru.practicum.ewm.model.enums.State;
import ru.practicum.ewm.model.enums.Status;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.common.dto.EndpointHit;
import ru.practicum.stats.common.dto.ViewStats;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utils.Constant.DATE_TIME_FORMATTER;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatsClient statsClient;
    @Value("${app.name}")
    private String appName;

    public EventServiceImpl(EventRepository eventRepository,
                            RequestRepository requestRepository,
                            CategoryRepository categoryRepository,
                            UserRepository userRepository,
                            StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.statsClient = statsClient;
    }

    @Override
    public List<Event> getAllAdmin(Set<Long> users, Set<State> states, Set<Long> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                   Integer from, Integer size) {
         List<Event> events = eventRepository.findAllByAdmin(users, states, categories,
                rangeStart, rangeEnd, PageRequest.of(from / size, size));

        setViewsAndConfirmedRequests(events);

        return events;
    }

    @Override
    @Transient
    public Event updateAdmin(Long eventId, Event event) {
        Event eventForUpd = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));

        updateEvent(event, eventForUpd);
        if (event.getStateActionAdmin() != null) {
            if (eventForUpd.getState().equals(State.PENDING)) {
                if (event.getStateActionAdmin().equals(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT)) {
                    if (LocalDateTime.now().plusHours(1).isAfter(eventForUpd.getEventDate())) {
                        throw new NotAvailableException(
                                "Start of the event must be no earlier than one hour from the publication date");
                    }
                    eventForUpd.setState(State.PUBLISHED);
                    eventForUpd.setPublishedOn(LocalDateTime.now());
                } else if (event.getStateActionAdmin().equals(UpdateEventAdminRequest.StateAction.REJECT_EVENT)) {
                    eventForUpd.setState(State.CANCELED);
                }
            } else {
                throw new NotAvailableException("Event must be in PENDING status");
            }
        }
        List<Event> events = List.of(eventRepository.save(eventForUpd));

        setViewsAndConfirmedRequests(events);

        return eventForUpd;
    }

    @Override
    public List<Event> getByUser(Long userId, Integer from, Integer size) {
        checkUser(userId);
        List<Event> events = eventRepository.findByInitiatorId(userId, PageRequest.of(from / size, size));

        setViewsAndConfirmedRequests(events);

        return events;
    }

    @Override
    @Transactional
    public Event add(Long userId, Event event) {
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id %d not found", userId)));
        Category category = categoryRepository.findById(event.getCategoryId()).orElseThrow(() ->
                new NotFoundException(String.format("User with id %d not found", event.getCategoryId())));

        event.setInitiator(initiator);
        event.setCategory(category);

        return eventRepository.save(event);
    }

    @Override
    public Event getByIdPrivate(Long userId, Long eventId) {
        checkUser(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new NotAvailableException("User is not the initiator of the event");
        }
        List<Event> events = List.of(event);

        setViewsAndConfirmedRequests(events);

        return event;
    }

    @Override
    public Event updatePrivate(Long userId, Long eventId, Event event) {
        checkUser(userId);
        Event eventForUpd = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
        if (eventForUpd.getInitiator().getId() != userId) {
            throw new NotAvailableException("User is not the initiator of the event");
        }
        if (eventForUpd.getState().equals(State.PUBLISHED)) {
            throw new NotAvailableException("Event with the status PUBLISHED cannot be updated");
        }
        updateEvent(event, eventForUpd);
        if (event.getStateActionUser() != null) {
            if (event.getStateActionUser().equals(UpdateEventUserRequest.StateAction.SEND_TO_REVIEW)) {
                eventForUpd.setState(State.PENDING);
            } else if (event.getStateActionUser().equals(UpdateEventUserRequest.StateAction.CANCEL_REVIEW)) {
                eventForUpd.setState(State.CANCELED);
            }
        }
        List<Event> events = List.of(eventRepository.save(eventForUpd));

        setViewsAndConfirmedRequests(events);

        return eventForUpd;
    }

    @Override
    public List<Request> getRequestByEvent(Long userId, Long eventId) {
        checkUser(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new NotAvailableException("User is not the initiator of the event");
        }
        return requestRepository.findByEventIdAndEventInitiatorId(eventId, userId);
    }

    @Override
    public Map<String, List<Request>> eventRequestsStatusUpdate(Long userId, Long eventId,
                                                                EventRequestStatusUpdateRequest updateRequest) {
        checkUser(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new NotAvailableException("User is not the initiator of the event");
        }
        List<Request> allRequests = requestRepository.findByEvent_InitiatorIdAndEventIdAndIdIn(userId,
                eventId,
                updateRequest.getRequestIds());
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        long confirmedRequestsCount = requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
        long freePoints = event.getParticipantLimit() - confirmedRequestsCount;

        if (updateRequest.getStatus().equals(Status.CONFIRMED)) {
            if (freePoints <= 0) {
                throw new NotAvailableException("The limit of requests to participate in the event has been reached");
            }
            for (Request request : allRequests) {
                if (request.getStatus().equals(Status.PENDING)) {
                    if (freePoints > 0) {
                        request.setStatus(Status.CONFIRMED);
                        confirmedRequests.add(requestRepository.save(request));
                        freePoints--;
                    } else {
                        request.setStatus(Status.REJECTED);
                        rejectedRequests.add(requestRepository.save(request));
                    }
                } else {
                    throw new NotAvailableException(
                            String.format("Request with id %d must be in PENDING status", request.getId()));
                }
            }
        } else if (updateRequest.getStatus().equals(Status.REJECTED)) {
            for (Request request : allRequests) {
                if (request.getStatus().equals(Status.PENDING)) {
                    request.setStatus(Status.REJECTED);
                    rejectedRequests.add(requestRepository.save(request));
                } else {
                    throw new NotAvailableException(
                            String.format("Request with id %d must be in PENDING status", request.getId()));
                }
            }
        }
        return Map.of("confirmed", confirmedRequests, "rejected", rejectedRequests);
    }

    @Override
    public List<Event> getAllPublic(String text, Set<Long> categories, Boolean paid,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    boolean onlyAvailable, EventSort eventSort, Integer from, Integer size,
                                    HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new IncorrectRangeException("End of the date range must be later than the start");
        }
        Pageable page = PageRequest.of(from / size, size);
        if (eventSort != null) {
            switch (eventSort) {
                case VIEWS:
                    page = PageRequest.of(from / size, size, Sort.by("views").descending());
                    break;
                case EVENT_DATE:
                    page = PageRequest.of(from / size, size, Sort.by("eventDate").descending());
                    break;
                default:
                    break;
            }
        }
        List<Event> events = eventRepository.findAllPublic(text, categories,
                paid, rangeStart, rangeEnd, onlyAvailable, page);

        sendStats(request);
        setViewsAndConfirmedRequests(events);

        return events;
    }

    @Override
    public Event getByIdPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
        List<Event> events = List.of(event);

        sendStats(request);
        setViewsAndConfirmedRequests(events);

        return events.get(0);
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
    }

    private void updateEvent(Event event, Event eventForUpd) {
        if (event.getEventDate() != null) {
            eventForUpd.setEventDate(event.getEventDate());
        }
        if (event.getCategoryId() != null) {
            Category category = categoryRepository.findById(event.getCategoryId()).orElseThrow(() ->
                    new NotFoundException(String.format("Category with id %d not found", event.getCategoryId())));
            eventForUpd.setCategory(category);
        }
        if (event.getAnnotation() != null) {
            eventForUpd.setAnnotation(event.getAnnotation());
        }
        if (event.getDescription() != null) {
            eventForUpd.setDescription(event.getDescription());
        }
        if (event.getLocation() != null) {
            eventForUpd.setLocation(event.getLocation());
        }
        if (event.getPaid() != null) {
            eventForUpd.setPaid(event.getPaid());
        }
        if (event.getParticipantLimit() != null) {
            eventForUpd.setParticipantLimit(event.getParticipantLimit());
        }
        if (event.getRequestModeration() != null) {
            eventForUpd.setRequestModeration(event.getRequestModeration());
        }
        if (event.getTitle() != null) {
            eventForUpd.setTitle(event.getTitle());
        }
    }

    private void sendStats(HttpServletRequest request) {

        EndpointHit endpointHit = EndpointHit.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.addStats(endpointHit);
    }

    private Map<Long, Long> getViews(List<Event> events) {
        LocalDateTime earliestEvent = events.stream()
                .filter(e -> e.getState().equals(State.PUBLISHED))
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.of(2000, 1, 1, 0, 0));
        String start = earliestEvent.format(DATE_TIME_FORMATTER);
        String end = LocalDateTime.now().plusMinutes(1).format(DATE_TIME_FORMATTER);

        List<String> uris = events.stream()
                .map(Event::getId)
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        List<ViewStats> stats = statsClient.getStats(start, end, uris, true);
        Map<Long, Long> viewsById = new HashMap<>();

        if (!stats.isEmpty()) {
            stats.forEach(stat -> {
                Long eventId = Long.parseLong(stat.getUri().split("/")[2]);
                viewsById.put(eventId, stat.getHits());
            });
        }
        return viewsById;
    }

    private Map<Long, Integer> getConfirmedRequests(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Request> confirmedRequests = requestRepository.findByStatusAndEventIdIn(Status.CONFIRMED, eventIds);

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
    }


    private void setViewsAndConfirmedRequests(List<Event> events) {
        Map<Long, Long> eventsViews = getViews(events);
        Map<Long, Integer> confirmedRequests = getConfirmedRequests(events);

        events.forEach(ev -> {
            ev.setViews(eventsViews.getOrDefault(ev.getId(), 0L));
            ev.setConfirmedRequests(confirmedRequests.getOrDefault(ev.getId(), 0));
        });
    }
}

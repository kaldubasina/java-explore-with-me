package ru.practicum.ewm.service.compilation;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.enums.State;
import ru.practicum.ewm.model.enums.Status;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.common.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utils.Constant.DATE_TIME_FORMATTER;

@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    public CompilationServiceImpl(CompilationRepository compilationRepository,
                                  EventRepository eventRepository,
                                  RequestRepository requestRepository,
                                  StatsClient statsClient) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.statsClient = statsClient;
    }

    @Override
    @Transactional
    public Compilation add(Compilation compilation, Set<Long> eventIds) {
        if (!eventIds.isEmpty()) {
            Set<Event> events = eventRepository.findAllByIdIn(eventIds);
            setViewsAndConfirmedRequest(events);
            compilation.setEvents(events);
        }
        return compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public Compilation update(Compilation compilation, Set<Long> eventIds, Long compId) {
        Compilation compForUpd = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id %d not found", compId)));
        if (compilation.getTitle() != null) compForUpd.setTitle(compilation.getTitle());

        if (compilation.getPinned() != null) compForUpd.setPinned(compilation.getPinned());

        if (!eventIds.isEmpty()) {
            Set<Event> events = eventRepository.findAllByIdIn(eventIds);
            setViewsAndConfirmedRequest(events);
            compForUpd.setEvents(events);
        }
        return compilationRepository.save(compForUpd);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Compilation with id %d not found", compId));
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<Compilation> getAll(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = compilationRepository.findAllByPinned(
                pinned, PageRequest.of(from / size, size));

        for (Compilation compilation : compilations) {
            Set<Event> events = compilation.getEvents();
            if (!events.isEmpty()) {
                setViewsAndConfirmedRequest(events);
                compilation.setEvents(events);
            }
        }
        return compilationRepository.findAllByPinned(pinned, PageRequest.of(from / size, size));
    }

    @Override
    public Compilation getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id %d not found", compId)));

        Set<Event> events = compilation.getEvents();
        if (!events.isEmpty()) {
            setViewsAndConfirmedRequest(events);
            compilation.setEvents(events);
        }
        return compilation;
    }

    private void setViewsAndConfirmedRequest(Set<Event> events) {
        Map<Long, Long> eventsViews = getViews(events);
        Map<Long, Integer> confirmedRequests = getConfirmedRequests(events);

        events.forEach(ev -> {
            ev.setViews(eventsViews.getOrDefault(ev.getId(), 0L));
            ev.setConfirmedRequests(confirmedRequests.getOrDefault(ev.getId(), 0));
        });
    }

    private Map<Long, Long> getViews(Set<Event> events) {
        LocalDateTime earliestEvent = events.stream()
                .filter(e -> e.getState().equals(State.PUBLISHED))
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.of(2000, 1, 1, 0, 0));
        String start = earliestEvent.format(DATE_TIME_FORMATTER);
        String end = LocalDateTime.now().format(DATE_TIME_FORMATTER);

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

    private Map<Long, Integer> getConfirmedRequests(Set<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Request> confirmedRequests = requestRepository.findByStatusAndEventIdIn(Status.CONFIRMED, eventIds);

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
    }
}

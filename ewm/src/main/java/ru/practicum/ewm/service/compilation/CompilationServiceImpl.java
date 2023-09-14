package ru.practicum.ewm.service.compilation;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.enums.Status;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.common.dto.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;
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
    public Compilation add(Compilation compilation, List<Long> eventIds) {
        if (!eventIds.isEmpty()) {
            Set<Event> events = new HashSet<>(eventRepository.findAllByIdIn(eventIds));
            setToEventsConfirmedRequestsAndViews(events);
            compilation.setEvents(events);
        }

        return compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public Compilation update(Compilation compilation, List<Long> eventIds, long compId) {
        Compilation compForUpd = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id %d not found", compId)));
        if (!compilation.getTitle().equals(compForUpd.getTitle())) compForUpd.setTitle(compilation.getTitle());

        if (compilation.getPinned() != null) compForUpd.setPinned(compilation.getPinned());

        Set<Event> events = new HashSet<>();
        if (!eventIds.isEmpty()) {
            events.addAll(eventRepository.findAllById(eventIds));
            setToEventsConfirmedRequestsAndViews(events);
        }
        compForUpd.setEvents(events);

        return compilationRepository.save(compForUpd);
    }

    @Override
    @Transactional
    public void delete(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Compilation with id %d not found", compId));
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<Compilation> getAll(boolean pinned, int from, int size) {
        List<Compilation> compilations = compilationRepository.findAllByPinned(
                pinned, PageRequest.of(from / size, size));

        for (Compilation compilation : compilations) {
            Set<Event> events = compilation.getEvents();
            if (!events.isEmpty()) {
                setToEventsConfirmedRequestsAndViews(events);
                compilation.setEvents(events);
            }
        }
        return compilations;
    }

    @Override
    public Compilation getById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id %d not found", compId)));

        Set<Event> events = compilation.getEvents();
        if (!events.isEmpty()) {
            setToEventsConfirmedRequestsAndViews(events);
            compilation.setEvents(events);
        }

        return compilation;
    }

    private void setToEventsConfirmedRequestsAndViews(Set<Event> events) {
        String start = URLEncoder.encode(LocalDateTime.MIN.format(DATE_TIME_FORMATTER), Charset.defaultCharset());
        String end = URLEncoder.encode(LocalDateTime.now().format(DATE_TIME_FORMATTER), Charset.defaultCharset());

        Set<String> uris = events.stream()
                .map(Event::getId)
                .map(id -> "/event/" + id)
                .collect(Collectors.toSet());

        List<ViewStats> stats = statsClient.getStats(start, end, uris, false);//.getBody();
        Map<Long, Long> viewsById = new HashMap<>();
        if (stats != null) {
            for (ViewStats stat : stats) {
                String[] uri = stat.getUri().split("/");
                viewsById.merge(Long.parseLong(uri[uri.length - 1]), stat.getHits(), Long::sum);
            }
        }

        for (Event event : events) {
            event.setViews(viewsById.getOrDefault(event.getId(), 0L));
            event.setConfirmedRequests(requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED));
        }
    }
}

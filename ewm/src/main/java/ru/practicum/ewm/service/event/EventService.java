package ru.practicum.ewm.service.event;

import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.enums.EventSort;
import ru.practicum.ewm.model.enums.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EventService {
    /**
     * admin methods
     */
    List<Event> getAllAdmin(Set<Long> users,
                            Set<State> states,
                            Set<Long> categories,
                            LocalDateTime rangeStart,
                            LocalDateTime rangeEnd,
                            Integer from, Integer size);

    Event updateAdmin(Long eventId, Event event);

    /**
     * public methods
     */
    List<Event> getAllPublic(String text, Set<Long> categories, Boolean paid,
                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                             boolean onlyAvailable, EventSort eventSort,
                             Integer from, Integer size,
                             HttpServletRequest request);

    Event getByIdPublic(Long eventId, HttpServletRequest request);

    /**
     * private methods
     */
    List<Event> getByUser(Long userId, Integer from, Integer size);

    Event add(Long userId, Event event);

    Event getByIdPrivate(Long userId, Long eventId);

    Event updatePrivate(Long userId, Long eventId, Event event);

    List<Request> getRequestByEvent(Long userId, Long eventId);

    Map<String, List<Request>> eventRequestsStatusUpdate(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest updateRequest);

}

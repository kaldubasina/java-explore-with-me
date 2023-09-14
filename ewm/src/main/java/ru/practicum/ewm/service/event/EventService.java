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
                            int from, int size);

    Event updateAdmin(long eventId, Event event);

    /**
     * public methods
     */
    List<Event> getAllPublic(String text, Set<Long> categories, Boolean paid,
                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                             boolean onlyAvailable, EventSort eventSort, int from, int size,
                             HttpServletRequest request);

    Event getByIdPublic(long eventId, HttpServletRequest request);

    /**
     * private methods
     */
    List<Event> getByUser(long userId, int from, int size);

    Event add(long userId, Event event);

    Event getByIdPrivate(long userId, long eventId);

    Event updatePrivate(long userId, long eventId, Event event);

    List<Request> getRequestByEvent(long userId, long eventId);

    Map<String, List<Request>> eventRequestsStatusUpdate(long userId, long eventId,
                                                         EventRequestStatusUpdateRequest updateRequest);

}

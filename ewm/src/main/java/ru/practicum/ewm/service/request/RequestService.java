package ru.practicum.ewm.service.request;

import ru.practicum.ewm.model.Request;

import java.util.List;

public interface RequestService {
    Request add(Long userId, Long eventId);

    List<Request> getRequests(Long userId);

    Request cancelRequest(Long userId, Long requestId);
}

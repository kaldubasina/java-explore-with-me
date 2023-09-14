package ru.practicum.ewm.service.request;

import ru.practicum.ewm.model.Request;

import java.util.List;

public interface RequestService {
    Request add(long userId, long eventId);

    List<Request> getRequests(long userId);

    Request cancelRequest(long userId, long requestId);
}

package ru.practicum.ewm.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.service.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService service;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;

    @GetMapping
    public Collection<EventShortDto> getOwn(@PathVariable long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getByUser(userId, from, size).stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto add(@PathVariable long userId, @RequestBody @Valid NewEventDto eventDto) {
        return eventMapper.toFullDto(service.add(userId, eventMapper.toEntity(eventDto)));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable long userId,
                                @PathVariable long eventId) {
        return eventMapper.toFullDto(service.getByIdPrivate(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long userId,
                               @PathVariable long eventId,
                               @RequestBody @Valid UpdateEventUserRequest eventRequest) {
        return eventMapper.toFullDto(service.updatePrivate(userId, eventId, eventMapper.toEntity(eventRequest)));
    }

    @GetMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDto> getRequestByEvent(@PathVariable long userId,
                                                                 @PathVariable long eventId) {
        return service.getRequestByEvent(userId, eventId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult eventRequestsStatusUpdate(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        Map<String, List<Request>> result = service.eventRequestsStatusUpdate(userId, eventId, updateRequest);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(
                        result.get("confirmed").stream()
                                .map(requestMapper::toDto)
                                .collect(Collectors.toList()))
                .rejectedRequests(
                        result.get("rejected").stream()
                                .map(requestMapper::toDto)
                                .collect(Collectors.toList()))
                .build();
    }
}

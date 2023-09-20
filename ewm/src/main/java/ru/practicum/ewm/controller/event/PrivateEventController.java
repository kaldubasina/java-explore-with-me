package ru.practicum.ewm.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class PrivateEventController {
    private final EventService service;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;

    @GetMapping
    public Collection<EventShortDto> getOwn(@PathVariable @Positive Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return service.getByUser(userId, from, size).stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto add(@PathVariable @Positive Long userId, @RequestBody @Valid NewEventDto eventDto) {
        return eventMapper.toFullDto(service.add(userId, eventMapper.toEntity(eventDto)));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable @Positive Long userId,
                                @PathVariable @Positive Long eventId) {
        return eventMapper.toFullDto(service.getByIdPrivate(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable @Positive Long userId,
                               @PathVariable @Positive Long eventId,
                               @RequestBody @Valid UpdateEventUserRequest eventRequest) {
        return eventMapper.toFullDto(service.updatePrivate(userId, eventId, eventMapper.toEntity(eventRequest)));
    }

    @GetMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDto> getRequestByEvent(@PathVariable @Positive Long userId,
                                                                 @PathVariable @Positive Long eventId) {
        return service.getRequestByEvent(userId, eventId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult eventRequestsStatusUpdate(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
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

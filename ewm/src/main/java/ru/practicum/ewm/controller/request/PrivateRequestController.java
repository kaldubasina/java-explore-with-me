package ru.practicum.ewm.controller.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.service.request.RequestService;

import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Validated
public class PrivateRequestController {
    private final RequestService service;
    private final RequestMapper mapper;

    @GetMapping
    public Collection<ParticipationRequestDto> getRequests(@PathVariable @Positive Long userId) {
        return service.getRequests(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@PathVariable @Positive Long userId,
                                       @RequestParam @Positive Long eventId) {
        return mapper.toDto(service.add(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long requestId) {
        return mapper.toDto(service.cancelRequest(userId, requestId));
    }
}

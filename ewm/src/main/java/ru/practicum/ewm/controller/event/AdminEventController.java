package ru.practicum.ewm.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.enums.State;
import ru.practicum.ewm.service.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utils.Constant.DATE_TIME_FORMAT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
public class AdminEventController {
    private final EventService service;
    private final EventMapper mapper;

    @GetMapping
    public Collection<EventFullDto> getAll(
            @RequestParam(required = false) Set<Long> users,
            @RequestParam(required = false) Set<State> states,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getAllAdmin(users, states, categories, rangeStart, rangeEnd, from, size).stream()
                .map(mapper::toFullDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable @Positive Long eventId,
                               @RequestBody @Valid UpdateEventAdminRequest eventRequest) {
        return mapper.toFullDto(service.updateAdmin(eventId, mapper.toEntity(eventRequest)));
    }
}

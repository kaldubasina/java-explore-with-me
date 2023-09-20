package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.stats.common.dto.EndpointHit;
import ru.practicum.stats.common.dto.ViewStats;
import ru.practicum.stats.server.mapper.StatsMapper;
import ru.practicum.stats.server.model.Stats;
import ru.practicum.stats.server.service.StatsService;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import static ru.practicum.stats.common.utils.Constant.DATE_TIME_FORMATTER;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;
    private final StatsMapper mapper;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public Stats addStats(@RequestBody @Valid EndpointHit endpointHit) {
        return service.add(mapper.toEntity(endpointHit));
    }

    @GetMapping("/stats")
    public Collection<ViewStats> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) Set<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(
                URLDecoder.decode(start, Charset.defaultCharset()), DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(
                URLDecoder.decode(end, Charset.defaultCharset()), DATE_TIME_FORMATTER);
        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start must be earlier than end");
        }
        return service.getStats(startDate, endDate, uris, unique);
    }
}

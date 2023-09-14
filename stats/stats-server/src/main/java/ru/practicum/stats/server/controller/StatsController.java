package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.common.dto.EndpointHit;
import ru.practicum.stats.common.dto.ViewStats;
import ru.practicum.stats.server.mapper.StatsMapper;
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

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addStats(@RequestBody @Valid EndpointHit endpointHit) {
        service.add(StatsMapper.INSTANCE.toEntity(endpointHit));
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
        return service.getStats(startDate, endDate, uris, unique);
    }
}

package ru.practicum.stats.client.controller;

import ru.practicum.stats.client.client.StatsClient;
import ru.practicum.stats.common.dto.EndpointHit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class StatsController {
    public final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveStats(@Valid @RequestBody EndpointHit statsDto) {
        return statsClient.addStats(statsDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(defaultValue = "") Set<String> uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {
        return statsClient.getStats(start, end, uris, unique);
    }
}

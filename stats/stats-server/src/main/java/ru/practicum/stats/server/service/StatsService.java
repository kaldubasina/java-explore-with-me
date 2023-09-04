package ru.practicum.stats.server.service;

import ru.practicum.stats.common.dto.ViewStats;
import ru.practicum.stats.server.model.Stats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface StatsService {
    void add(Stats stats);

    Collection<ViewStats> getStats(LocalDateTime start, LocalDateTime end, Set<String> uris, boolean unique);
}

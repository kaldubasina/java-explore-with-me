package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.common.dto.ViewStats;
import ru.practicum.stats.server.model.Stats;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    @Transactional
    public void add(Stats stats) {
        repository.save(stats);
    }

    @Override
    public Collection<ViewStats> getStats(LocalDateTime start,
                                          LocalDateTime end,
                                          Set<String> uris,
                                          boolean unique) {
        return unique ? repository.findUniqueStats(start, end, uris) :
                repository.findAllStats(start, end, uris);
    }
}

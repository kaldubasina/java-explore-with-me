package ru.practicum.ewm.service.compilation;

import ru.practicum.ewm.model.Compilation;

import java.util.List;
import java.util.Set;

public interface CompilationService {
    Compilation add(Compilation compilation, Set<Long> eventIds);

    Compilation update(Compilation compilation, Set<Long> eventIds, Long compId);

    void delete(Long compId);

    List<Compilation> getAll(Boolean pinned, Integer from, Integer size);

    Compilation getById(Long compId);
}

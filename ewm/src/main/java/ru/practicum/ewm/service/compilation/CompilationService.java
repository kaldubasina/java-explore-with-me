package ru.practicum.ewm.service.compilation;

import ru.practicum.ewm.model.Compilation;

import java.util.List;

public interface CompilationService {
    Compilation add(Compilation compilation, List<Long> eventIds);

    Compilation update(Compilation compilation, List<Long> eventIds, Long compId);

    void delete(Long compId);

    List<Compilation> getAll(Boolean pinned, Integer from, Integer size);

    Compilation getById(Long compId);
}

package ru.practicum.ewm.service.compilation;

import ru.practicum.ewm.model.Compilation;

import java.util.List;

public interface CompilationService {
    Compilation add(Compilation compilation, List<Long> eventIds);

    Compilation update(Compilation compilation, List<Long> eventIds, long compId);

    void delete(long compId);

    List<Compilation> getAll(boolean pinned, int from, int size);

    Compilation getById(long compId);
}

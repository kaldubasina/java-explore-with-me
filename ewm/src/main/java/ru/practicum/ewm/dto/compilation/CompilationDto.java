package ru.practicum.ewm.dto.compilation;

import lombok.Data;
import ru.practicum.ewm.dto.event.EventShortDto;

import java.util.Set;

@Data
public class CompilationDto {
    private long id;
    private boolean pinned;
    private String title;
    private Set<EventShortDto> events;
}

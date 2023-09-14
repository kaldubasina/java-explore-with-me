package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.model.Event;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class, LocationMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    Event toEntity(NewEventDto eventDto);

    Event toEntity(UpdateEventAdminRequest eventDto);

    Event toEntity(UpdateEventUserRequest eventDto);

    EventFullDto toFullDto(Event event);

    EventShortDto toShortDto(Event event);
}

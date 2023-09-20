package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.common.dto.EndpointHit;
import ru.practicum.stats.server.model.Stats;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    Stats toEntity(EndpointHit endpointHit);
}

package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.stats.common.dto.EndpointHit;
import ru.practicum.stats.server.model.Stats;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    StatsMapper INSTANCE = Mappers.getMapper(StatsMapper.class);

    Stats fromDto(EndpointHit endpointHit);
}

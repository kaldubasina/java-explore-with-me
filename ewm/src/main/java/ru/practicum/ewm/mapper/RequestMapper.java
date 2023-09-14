package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    ParticipationRequestDto toDto(Request request);
}

package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.model.Compilation;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {
    CompilationDto toDto(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    Compilation toEntity(NewCompilationDto compilationDto);

    @Mapping(target = "events", ignore = true)
    Compilation toEntity(UpdateCompilationRequest compilationDto);
}

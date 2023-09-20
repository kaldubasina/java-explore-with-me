package ru.practicum.ewm.controller.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.service.compilation.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationController {
    private final CompilationService service;
    private final CompilationMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@RequestBody @Valid NewCompilationDto compilationDto) {
        return mapper.toDto(service.add(mapper.toEntity(compilationDto), compilationDto.getEvents()));
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@RequestBody @Valid UpdateCompilationRequest compilationRequest,
                                 @PathVariable @Positive Long compId) {
        return mapper.toDto(service.update(mapper.toEntity(compilationRequest),
                compilationRequest.getEvents(),
                compId));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long compId) {
        service.delete(compId);
    }
}

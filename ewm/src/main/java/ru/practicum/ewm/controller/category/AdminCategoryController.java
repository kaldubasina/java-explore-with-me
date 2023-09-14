package ru.practicum.ewm.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.service.category.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Validated
public class AdminCategoryController {
    private final CategoryService service;
    private final CategoryMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@RequestBody @Valid NewCategoryDto categoryDto) {
        return mapper.toDto(service.add(mapper.toEntity(categoryDto)));
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@RequestBody @Valid CategoryDto categoryDto, @PathVariable @Positive Long catId) {
        return mapper.toDto(service.update(mapper.toEntity(categoryDto), catId));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long catId) {
        service.delete(catId);
    }
}

package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(NewCategoryDto categoryDto);

    Category toEntity(CategoryDto categoryDto);

    CategoryDto toDto(Category category);
}

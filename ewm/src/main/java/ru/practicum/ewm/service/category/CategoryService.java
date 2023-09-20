package ru.practicum.ewm.service.category;

import ru.practicum.ewm.model.Category;

import java.util.List;

public interface CategoryService {
    Category add(Category category);

    Category update(Category category, Long catId);

    void delete(Long catId);

    List<Category> getAll(Integer from, Integer size);

    Category getById(Long catId);
}

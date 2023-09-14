package ru.practicum.ewm.service.category;

import ru.practicum.ewm.model.Category;

import java.util.List;

public interface CategoryService {
    Category add(Category category);

    Category update(Category category, long catId);

    void delete(long catId);

    List<Category> getAll(int from, int size);

    Category getById(long catId);
}

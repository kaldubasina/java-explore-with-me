package ru.practicum.ewm.service.category;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.AlreadyExistException;
import ru.practicum.ewm.exception.NotAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryServiceImpl(CategoryRepository repository, EventRepository eventRepository) {
        this.categoryRepository = repository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public Category add(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new AlreadyExistException(String.format("Category with name %s already exists", category.getName()));
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category update(Category category, Long catId) {
        Category catForUpd = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id %d not found", catId)));
        if (categoryRepository.existsByName(category.getName())) {
            throw new AlreadyExistException(String.format("Category with name %s already exists", category.getName()));
        }
        if (!catForUpd.getName().equals(category.getName())) {
            catForUpd.setName(category.getName());
        }
        return categoryRepository.save(catForUpd);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Category with id %d not found", catId));
        }
        if (eventRepository.existsByCategory_Id(catId)) {
            throw new NotAvailableException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<Category> getAll(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).getContent();
    }

    @Override
    public Category getById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id %d not found", catId)));
    }
}

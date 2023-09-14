package ru.practicum.ewm.service.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.AlreadyExistException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public User add(User user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new AlreadyExistException(String.format("User with email %s already exists", user.getEmail()));
        }
        return repository.save(user);
    }

    @Override
    public List<User> getByIds(Set<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            return repository.findAll(page).getContent();
        }
        return repository.findByIdIn(ids, page);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        if (!repository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
        repository.deleteById(userId);
    }
}

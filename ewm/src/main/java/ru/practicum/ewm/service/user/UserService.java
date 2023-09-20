package ru.practicum.ewm.service.user;

import ru.practicum.ewm.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User add(User user);

    List<User> getByIds(Set<Long> ids, Integer from, Integer size);

    void delete(Long userId);
}

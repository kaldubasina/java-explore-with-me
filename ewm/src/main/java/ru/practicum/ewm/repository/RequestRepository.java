package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.enums.Status;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByEventIdAndRequesterId(long eventId, long userId);

    int countByEventIdAndStatus(long eventId, Status status);

    List<Request> findByRequesterId(long userId);

    List<Request> findByEventIdAndEventInitiatorId(long eventId, long userId);

    List<Request> findByEventIdAndEventInitiatorIdAndIdIn(long userId, long eventId, List<Long> requestIds);
}

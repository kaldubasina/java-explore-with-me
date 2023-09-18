package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.enums.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    Set<Event> findAllByIdIn(Set<Long> eventIds);

    @Query("SELECT e FROM Event e " +
            "WHERE (COALESCE(:users, null) IS NULL OR e.initiator.id in :users) " +
            "AND (COALESCE(:states, null) IS NULL OR e.state in :states) " +
            "AND (COALESCE(:categories, null) IS NULL OR e.category.id in :categories) " +
            "AND (COALESCE(:rangeStart, null) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (COALESCE(:rangeEnd, null) IS NULL OR e.eventDate <= :rangeEnd)")
    List<Event> findAllByAdmin(Set<Long> users, Set<State> states, Set<Long> categories,
                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                               Pageable page);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (COALESCE(:text, null) IS NULL OR (lower(e.annotation) LIKE lower(CONCAT('%', :text, '%')) " +
            "OR lower(e.description) LIKE lower(CONCAT('%', :text, '%')))) " +
            "AND (COALESCE(:categories, null) IS NULL OR e.category.id in :categories) " +
            "AND (COALESCE(:paid, null) IS NULL OR e.paid = :paid) " +
            "AND e.eventDate >= :rangeStart " +
            "AND (COALESCE(:rangeEnd, null) is null or e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = false OR e.id in " +
            "(SELECT r.event.id " +
            "FROM Request r " +
            "WHERE r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id " +
            "HAVING e.participantLimit - COUNT(r.id) > 0 " +
            "ORDER BY COUNT(r.id)))")
    List<Event> findAllPublic(String text, Set<Long> categories, Boolean paid,
                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                              boolean onlyAvailable, Pageable page);

    Optional<Event> findByIdAndState(long eventId, State state);

    List<Event> findByInitiatorId(long userId, Pageable page);

    boolean existsByCategory_Id(long catId);
}

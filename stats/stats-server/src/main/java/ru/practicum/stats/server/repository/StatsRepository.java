package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.common.dto.ViewStats;
import ru.practicum.stats.server.model.Stats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("SELECT new ru.practicum.stats.common.dto.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats s WHERE (s.timestamp BETWEEN :start AND :end) " +
            "AND (COALESCE(:uris, null) IS NULL OR s.uri in :uris) " +
            "GROUP BY s.app, s.uri ORDER BY COUNT(s.ip) DESC")
    Collection<ViewStats> findAllStats(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("uris") Set<String> uris);

    @Query("SELECT new ru.practicum.stats.common.dto.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats s WHERE (s.timestamp BETWEEN :start AND :end) " +
            "AND (COALESCE(:uris, null) IS NULL OR s.uri in :uris) " +
            "GROUP BY s.app, s.uri ORDER BY COUNT(DISTINCT s.ip) DESC")
    Collection<ViewStats> findUniqueStats(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("uris") Set<String> uris);
}

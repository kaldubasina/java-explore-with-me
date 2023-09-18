package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.Constant.DATE_TIME_FORMAT;

public interface EventShort {
    long getId();
    String getAnnotation();
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime getEventDate();
    boolean getPaid();
    String getTitle();
    Category getCategory();
    interface Category {
        long getId();
        String getName();
    }
    Initiator getInitiator();
    interface Initiator {
        long getId();
        String getName();
    }
    int confirmedRequests = 0;
    long views = 0;
}

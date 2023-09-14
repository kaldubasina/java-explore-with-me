package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.validation.EventDate;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static ru.practicum.stats.common.utils.Constant.DATE_TIME_FORMAT;

@Data
abstract class UpdateEventRequest {
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long categoryId;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    @EventDate
    private LocalDateTime eventDate;
    @Length(min = 20, max = 7000)
    private String description;
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    @Length(min = 3, max = 120)
    private String title;
}

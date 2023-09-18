package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.validation.EventDate;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static ru.practicum.stats.common.utils.Constant.DATE_TIME_FORMAT;

@Data
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @JsonProperty("category")
    private long categoryId;

    @NotNull
    @EventDate
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @NotNull
    @Valid
    private LocationDto location;

    @NotBlank
    @Length(min = 3, max = 120)
    private String title;

    private boolean paid;

    @PositiveOrZero
    private Integer participantLimit = 0;

    private boolean requestModeration = true;
}

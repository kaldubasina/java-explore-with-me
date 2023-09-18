package ru.practicum.ewm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.model.enums.Status;

import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.Constant.DATE_TIME_FORMAT;

@Data
public class ParticipationRequestDto {
    private Long id;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private Status status;
}

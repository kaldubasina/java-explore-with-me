package ru.practicum.ewm.dto.request;

import lombok.Data;
import ru.practicum.ewm.model.enums.Status;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Изменение статуса запроса на участие в событии текущего пользователя
 */
@Data
public class EventRequestStatusUpdateRequest {
    @NotNull
    private Status status;
    @NotNull
    private List<Long> requestIds;
}

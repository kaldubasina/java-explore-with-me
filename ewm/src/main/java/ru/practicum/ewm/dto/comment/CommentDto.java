package ru.practicum.ewm.dto.comment;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private long id;

    private String text;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    private Long event;

    private Long author;
}

package ru.practicum.ewm.dto.comment;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class NewCommentDto {
    @NotBlank
    @Length(max = 255)
    private String text;
}

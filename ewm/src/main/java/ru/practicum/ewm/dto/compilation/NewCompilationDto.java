package ru.practicum.ewm.dto.compilation;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewCompilationDto {
    private List<Long> eventIds = new ArrayList<>();
    private boolean pinned;
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
}

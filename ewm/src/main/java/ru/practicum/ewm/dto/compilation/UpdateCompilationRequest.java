package ru.practicum.ewm.dto.compilation;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateCompilationRequest {
    private List<Long> eventIds = new ArrayList<>();
    private Boolean pinned;
    @Length(min = 1, max = 50)
    private String title;
}

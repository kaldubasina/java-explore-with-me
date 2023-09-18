package ru.practicum.ewm.dto.compilation;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@Data
public class UpdateCompilationRequest {
    private Set<Long> events = new HashSet<>();
    private Boolean pinned;
    @Length(min = 1, max = 50)
    private String title;
}

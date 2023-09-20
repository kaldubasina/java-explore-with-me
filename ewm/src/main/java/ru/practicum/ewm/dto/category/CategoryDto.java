package ru.practicum.ewm.dto.category;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {
    private long id;
    @NotBlank
    @Length(min = 1, max = 50)
    private String name;
}

package ru.practicum.ewm.dto.event;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class LocationDto {
    @Min(-90)
    @Max(90)
    @NotNull
    private float lat;

    @Min(-180)
    @Max(180)
    @NotNull
    private float lon;
}

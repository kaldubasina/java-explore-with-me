package ru.practicum.ewm.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDate, LocalDateTime> {
    LocalDateTime validDate;

    @Override
    public void initialize(EventDate constraintAnnotation) {
        validDate = LocalDateTime.now().plusHours(2L);
    }

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext constraintValidatorContext) {
        return eventDate == null || eventDate.isAfter(validDate.plusHours(2L));
    }
}

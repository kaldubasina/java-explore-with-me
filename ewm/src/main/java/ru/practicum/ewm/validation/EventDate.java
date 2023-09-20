package ru.practicum.ewm.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
public @interface EventDate {
    String message() default "Date and time of the event cannot be earlier than two hours from the current moment";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

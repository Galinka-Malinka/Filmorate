package ru.yandex.practicum.filmorate.validation;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DateEqualsOrAfterValidator.class)

public @interface DateEqualsOrAfter {
    String message() default "{DateEqualsOrAfter.invalid}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
    String value() default "0001-01-01";
}

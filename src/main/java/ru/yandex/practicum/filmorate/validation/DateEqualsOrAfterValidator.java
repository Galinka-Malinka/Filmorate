package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateEqualsOrAfterValidator implements ConstraintValidator<DateEqualsOrAfter, LocalDate> {
    private String value;

    @Override
    public void initialize(DateEqualsOrAfter constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate s, ConstraintValidatorContext constraintValidatorContext) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(value, dateTimeFormatter);

        return s != null && (s.isEqual(localDate) || s.isAfter(localDate));
    }
}

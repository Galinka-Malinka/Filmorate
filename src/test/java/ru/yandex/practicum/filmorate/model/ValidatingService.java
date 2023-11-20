package ru.yandex.practicum.filmorate.model;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Service
public class ValidatingService {

    private final Validator validator;

    public ValidatingService(Validator validator) {
        this.validator = validator;
    }

    public void validateSimpleUser(User user) throws ValidationException {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации");
        }
    }

    public void validateSimpleFilm(Film film) throws ValidationException {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации");
        }
    }

    public void validateSimpleReview(Review review) throws ValidationException {
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации");
        }
    }
}

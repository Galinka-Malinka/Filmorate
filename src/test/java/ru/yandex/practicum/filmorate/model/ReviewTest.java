package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ReviewTest {
    @Autowired
    ValidatingService service;

    @Test
    public void createInvalidReviewsTest() {
        Collection<Review> reviews = new ArrayList<>();

        //Нет контента
        reviews.add(Review.builder()
                .positive(true)
                .filmId(1L)
                .userId(1L)
                .build());

        //Пустой контента
        reviews.add(Review.builder()
                .content("")
                .positive(true)
                .filmId(1L)
                .userId(1L)
                .build());

        //Нет id фильма
        reviews.add(Review.builder()
                .content("Это обзор фильма.")
                .positive(true)
                .userId(1L)
                .build());

        //Нет id пользователя
        reviews.add(Review.builder()
                .content("Это обзор фильма.")
                .positive(true)
                .filmId(1L)
                .build());

        //Нет вида обзора
        reviews.add(Review.builder()
                .content("Это обзор фильма")
                .filmId(1L)
                .userId(1L)
                .build());

        reviews.forEach(x -> assertThrows(ValidationException.class, () -> service.validateSimpleReview(x)));
    }
}

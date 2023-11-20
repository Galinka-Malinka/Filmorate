package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmTest {
    @Autowired
    ValidatingService service;

    @Test
    public void createInvalidFilmTest() {
        Collection<Film> films = new ArrayList<>();

        //Нет названия
        films.add(Film.builder()
                .description("Description")
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(50)
                .build());

        //Пустое названия
        films.add(Film.builder()
                .name("")
                .description("Description")
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(50)
                .build());

        //Нет даты релиза
        films.add(Film.builder()
                .name("Name")
                .description("Description")
                .duration(50)
                .build());

        //Дата релиза раньше 1895 года
        films.add(Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1000,1,1))
                .duration(50)
                .build());

        //Нет продолжительности
        films.add(Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1000,1,1))
                .build());

        //Продолжительность отрицательная
        films.add(Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1000,1,1))
                .duration(-50)
                .build());

        films.forEach(x -> assertThrows(ValidationException.class, () -> service.validateSimpleFilm(x)));
    }
}

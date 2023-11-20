package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.SortType;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Optional<Film> addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Optional<Film> updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Optional<Film> getFilm(@PathVariable Long id) throws ValidationException {
        return filmService.getFilm(id);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId,
                        @RequestParam(defaultValue = "10") @Positive Integer mark) throws ValidationException {
        filmService.addLike(id, userId, mark);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) throws ValidationException {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilmsByLikes(@RequestParam(defaultValue = "10") @Positive Integer count,
                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer genreId,
                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer year) {
        return filmService.getTopByLikes(count, genreId, year);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@NotNull @Positive @RequestParam Long userId,
                                           @NotNull @Positive @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getDirectorFilms(@PathVariable Long directorId,
                                             @RequestParam(value = "sortBy") String sort) {
        SortType sortType = SortType.getSortTypeByString(sort);
        if (sortType == null) {
            throw new ValidationException("Не верно введённый параметр сортировки : " + sort);
        }
        return filmService.getDirectorFilms(directorId, sortType);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable long filmId) {
        filmService.deleteFilmById(filmId);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(@RequestParam String query,
                                        @RequestParam List<String> by) {
        return filmService.searchFilms(query, by);
    }
}

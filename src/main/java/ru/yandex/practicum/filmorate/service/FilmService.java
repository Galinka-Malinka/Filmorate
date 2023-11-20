package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.SortType;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Optional<Film> addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Optional<Film> updateFilm(Film film) {
        if (filmStorage.filmNotExist(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        return filmStorage.updateFilm(film);
    }

    public Optional<Film> getFilm(Long id) {
        Optional<Film> foundFilm = filmStorage.getFilm(id);
        if (foundFilm.isEmpty()) {
            throw new NotFoundException("Фильм с id " + id + " не найден.");
        }
        return foundFilm;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void addLike(Long id, Long userId, Integer mark) {
        if (filmStorage.filmNotExist(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден.");
        }
        if (userStorage.userNotExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        if (mark <= 0 || mark > 10) {
            throw new ValidationException("Оценка фильма может быть только от 1 до 10");
        }
        filmStorage.addLike(id, userId, mark);
    }

    public void removeLike(Long id, Long userId) {
        if (filmStorage.filmNotExist(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден.");
        }
        if (userStorage.userNotExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        filmStorage.removeLike(id, userId);
    }

    public Collection<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Optional<Mpa> getMpaById(int id) {
        Optional<Mpa> mpa = filmStorage.getMpaById(id);
        if (mpa.isEmpty()) {
            throw new NotFoundException("Рейтинга с id " + id + " не существует.");
        }
        return mpa;
    }

    public Collection<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Optional<Genre> getGenreById(int id) {
        Optional<Genre> genre = filmStorage.getGenreById(id);
        if (genre.isEmpty()) {
            throw new NotFoundException("Жанра с id " + id + " не существует.");
        }
        return genre;
    }

    public Optional<Director> addDirector(Director director) {
        return filmStorage.addDirector(director);
    }

    public Optional<Director> updateDirector(Director director) {
        filmStorage.getDirector(director.getId())
                .orElseThrow(() -> new NotFoundException("Режиссёра с id " + director.getId() + " не существует."));

        return filmStorage.updateDirector(director);
    }

    public Collection<Director> getAllDirectors() {
        return filmStorage.getAllDirectors();
    }

    public Optional<Director> getDirector(Long id) {
        Optional<Director> director = filmStorage.getDirector(id);
        if (director.isEmpty()) {
            throw new NotFoundException("Режиссёра с id " + id + " не существует.");
        }
        return director;
    }

    public void removeDirector(Long id) {
        filmStorage.getDirector(id)
                .orElseThrow(() -> new NotFoundException("Режиссёра с id " + id + " не существует."));

        filmStorage.removeDirector(id);
    }

    public Collection<Film> getDirectorFilms(Long directorId, SortType sort) {
        if (filmStorage.directorNotExist(directorId)) {
            throw new NotFoundException("Режиссёра с id " + directorId + " не существует.");
        }
        return filmStorage.getDirectorFilms(directorId, sort);
    }

    public void deleteFilmById(Long id) {
        if (filmStorage.filmNotExist(id)) {
            throw new NotFoundException("Нет фильма с таким id");
        }
        filmStorage.deleteFilmById(id);
    }

    public Collection<Film> getTopByLikes(Integer count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public Collection<Film> searchFilms(String query, List<String> by) {
        if (by.size() > 2 || (!by.contains("director") & !by.contains("title"))) {
            throw new ValidationException("Некорректный запрос. Можно искать только по режиссёру" +
                    " и/или названию фильма.");
        }
        return filmStorage.searchFilms(query, by);
    }

    public Collection<Film> getCommonFilms(Long userId, Long otherId) {
        if (userStorage.userNotExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        if (userStorage.userNotExist(otherId)) {
            throw new NotFoundException("Пользователь с id " + otherId + " не найден.");
        }
        if (userId.equals(otherId)) {
            throw new ValidationException("Введён один и тот же Id. Для получения общих фильмов необходимо ввести" +
                    " Id друга. ");
        }
        return filmStorage.getCommonFilms(userId, otherId);
    }
}

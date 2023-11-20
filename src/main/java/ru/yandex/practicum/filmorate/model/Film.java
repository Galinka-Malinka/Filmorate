package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validation.DateEqualsOrAfter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    final Long id;
    @NotNull
    @NotBlank(message = "Название не может быть пустым.")
    final String name;
    @Size(max = 200, message = "Максимальная длина описания 200 символов.")
    final String description;
    @NotNull
    @DateEqualsOrAfter(value = "1895-12-28", message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    final LocalDate releaseDate;
    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    final int duration;
    final Mpa mpa;
    final Set<Long> likes;
    final Set<Genre> genres;
    final Set<Director> directors;

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void removeLike(Long userId) {
        likes.removeIf(p -> p.equals(userId));
    }

    public Integer getMpaId() {
        return (mpa == null ? null : mpa.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return duration == film.duration && id.equals(film.id) && name.equals(film.name) && description.equals(film.description) && releaseDate.equals(film.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, releaseDate, duration);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", getMpaId());
        return values;
    }
}

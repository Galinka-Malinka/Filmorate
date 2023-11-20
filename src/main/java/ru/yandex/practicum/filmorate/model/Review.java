package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Review {
    @JsonProperty("reviewId")
    private final Long id;
    @NotNull
    @NotBlank(message = "Текст обзора не может быть пустым.")
    private final String content;
    @JsonProperty("isPositive")
    @NotNull
    private final Boolean positive;
    @NotNull
    private final Long userId;
    @NotNull
    private final Long filmId;
    private final int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("positive", positive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        return values;
    }
}

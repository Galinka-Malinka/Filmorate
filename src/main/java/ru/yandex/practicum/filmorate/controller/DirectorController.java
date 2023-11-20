package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {
    private final FilmService filmService;

    @PostMapping
    public Optional<Director> addDirector(@Valid @RequestBody Director director) {
        return filmService.addDirector(director);
    }

    @PutMapping
    public Optional<Director> updateDirector(@RequestBody Director director) {
        return filmService.updateDirector(director);
    }

    @GetMapping
    public Collection<Director> getAllDirectors() {
        return filmService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Optional<Director> getDirector(@PathVariable Long id) {
        return filmService.getDirector(id);
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable Long id) {
        filmService.removeDirector(id);
    }
}

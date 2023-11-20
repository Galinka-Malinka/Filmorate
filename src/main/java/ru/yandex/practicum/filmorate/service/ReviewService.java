package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class ReviewService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;

    public ReviewService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage,
                         @Qualifier("reviewDbStorage") ReviewStorage reviewStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.reviewStorage = reviewStorage;
    }

    public Optional<Review> addReview(Review review) {
        if (filmStorage.filmNotExist(review.getFilmId())) {
            throw new NotFoundException("Фильм с id " + review.getFilmId() + " не найден.");
        } else if (userStorage.userNotExist(review.getUserId())) {
            throw new NotFoundException("Пользователь с id " + review.getUserId() + " не найден.");
        }
        return reviewStorage.addReview(review);
    }

    public Optional<Review> updateReview(Review review) {
        if (reviewStorage.reviewNotExist(review.getId())) {
            throw new NotFoundException("Обзор с id " + review.getId() + " не найден.");
        }
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long id) {
        if (reviewStorage.reviewNotExist(id)) {
            throw new NotFoundException("Обзор с id " + id + " не найден.");
        }
        reviewStorage.deleteReview(id);
    }

    public Optional<Review> getReview(Long id) {
        Optional<Review> foundReview = reviewStorage.getReview(id);
        if (foundReview.isEmpty()) {
            throw new NotFoundException("Обзор с id " + id + " не найден.");
        }
        return foundReview;
    }

    public Collection<Review> getReviews(Long filmId, int count) {
        if (filmId != null && filmStorage.filmNotExist(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        return reviewStorage.getReviews(filmId, count);
    }

    public void addLikeDislike(Long id, Long userId, boolean isLike) {
        if (reviewStorage.reviewNotExist(id)) {
            throw new NotFoundException("Обзор с id " + id + " не найден.");
        } else if (userStorage.userNotExist(userId)) {
            throw new NotFoundException("Пользователя с id " + userId + " не найден.");
        }
        reviewStorage.addLikeDislike(id, userId, isLike);
    }

    public void deleteLikeDislike(Long id, Long userId, boolean isLike) {
        if (reviewStorage.reviewNotExist(id)) {
            throw new NotFoundException("Обзор с id " + id + " не найден.");
        } else if (userStorage.userNotExist(userId)) {
            throw new NotFoundException("Пользователя с id " + userId + " не найден.");
        }
        reviewStorage.deleteLikeDislike(id, userId, isLike);
    }
}

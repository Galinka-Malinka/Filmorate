package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> addReview(Review review);

    Optional<Review> updateReview(Review review);

    void deleteReview(Long id);

    Optional<Review> getReview(Long id);

    Collection<Review> getReviews(Long filmId, int count);

    boolean reviewExist(Long id);

    boolean reviewNotExist(Long id);

    void addLikeDislike(Long id, Long userId, boolean isLike);

    void deleteLikeDislike(Long id, Long userId, boolean isLike);
}

package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final ReviewDbStorage reviewStorage;
    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_DATA_FILE = "src" + File.separator + "main" + File.separator + "resources" +
            File.separator + "data.sql";

    @BeforeEach
    public void beforeEachTest() throws IOException {
        String sqlQuery = new String(Files.readAllBytes(Paths.get(SQL_DATA_FILE)));
        jdbcTemplate.execute(sqlQuery);

        findOrCreateFilm();
        findOrCreateUser();
    }

    private void findOrCreateFilm() {
        if (filmStorage.filmNotExist(1L)) {
            filmStorage.addFilm(Film.builder()
                    .name("Film name")
                    .description("Film description")
                    .releaseDate(LocalDate.of(2000, 1, 1))
                    .duration(60)
                    .build());
        }
    }

    private void findOrCreateUser() {
        if (userStorage.userNotExist(1L)) {
            userStorage.addUser(User.builder()
                    .login("NewUser")
                    .email("user email")
                    .name("New user")
                    .birthday(LocalDate.of(2000, 1, 1))
                    .build());
        }
    }

    @Test
    public void testAddReview() {
        Review newReview = Review.builder()
                .content("Это обзор фильма")
                .positive(true)
                .filmId(1L)
                .userId(1L)
                .build();

        Optional<Review> optionalReview = reviewStorage.addReview(newReview);

        assertThat(optionalReview)
                .isPresent();
    }

    @Test
    public void testUpdateReview() {
        Review newReview = Review.builder()
                .content("Это обзор фильма")
                .positive(true)
                .filmId(1L)
                .userId(1L)
                .build();

        Optional<Review> optionalReview = reviewStorage.addReview(newReview);

        assertThat(optionalReview)
                .isPresent();

        Review reviewForUpdate = Review.builder()
                .id(optionalReview.get().getId())
                .content("Обновленный обзор")
                .positive(newReview.getPositive())
                .filmId(newReview.getFilmId())
                .userId(newReview.getUserId())
                .build();

        optionalReview = reviewStorage.updateReview(reviewForUpdate);

        assertThat(optionalReview)
                .isPresent();

        newReview = optionalReview.get();

        assertThat(newReview.getContent())
                .isEqualTo("Обновленный обзор");
    }

    @Test
    public void testUpdateReviewWithWrongId() {
        Review newReview = Review.builder()
                .id(999L)
                .content("Это обзор фильма")
                .positive(true)
                .filmId(1L)
                .userId(1L)
                .build();

        Optional<Review> optionalReview = reviewStorage.updateReview(newReview);

        assertThat(optionalReview)
                .isEmpty();
    }

    @Test
    public void testGetReview() {
        Review newReview = Review.builder()
                .content("Это обзор фильма")
                .positive(true)
                .filmId(1L)
                .userId(1L)
                .build();

        Optional<Review> optionalReview = reviewStorage.addReview(newReview);

        assertThat(optionalReview)
                .isPresent();

        Optional<Review> optionalGetReview = reviewStorage.getReview(optionalReview.get().getId());

        assertThat(optionalGetReview)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("id", optionalReview.get().getId())
                );
    }

    @Test
    public void testGetReviewWidthWrongId() {
        Optional<Review> optionalReview = reviewStorage.getReview(999L);

        assertThat(optionalReview)
                .isEmpty();
    }

    @Test
    public void testGetAllReviews() {
        Review newReview = Review.builder()
                .content("Это обзор фильма")
                .positive(true)
                .filmId(1L)
                .userId(1L)
                .build();

        Optional<Review> optionalReview = reviewStorage.addReview(newReview);

        assertThat(optionalReview)
                .isPresent();

        Collection<Review> reviews = reviewStorage.getReviews(null, 10);

        assertThat(reviews)
                .isNotEmpty();
    }

    @Test
    public void testGetReviewsByFilmId() {
        Review newReview = Review.builder()
                .content("Это обзор фильма")
                .positive(true)
                .filmId(1L)
                .userId(1L)
                .build();

        Optional<Review> optionalReview = reviewStorage.addReview(newReview);

        assertThat(optionalReview)
                .isPresent();

        Collection<Review> reviews = reviewStorage.getReviews(1L, 10);

        assertThat(reviews)
                .isNotEmpty();
    }

    @Test
    public void testGetReviewsByWrongFilmId() {
        Collection<Review> reviews = reviewStorage.getReviews(999L, 10);

        assertThat(reviews)
                .isEmpty();
    }

    @Test
    public void testReviewAddDeleteLike() {
        Review newReview = Review.builder()
                .content("Это обзор фильма")
                .positive(true)
                .filmId(1L)
                .userId(1L)
                .build();

        Optional<Review> optionalReview = reviewStorage.addReview(newReview);

        assertThat(optionalReview)
                .isPresent();

        // Добавление дайка

        reviewStorage.addLikeDislike(optionalReview.get().getId(), 1L, true);

        optionalReview = reviewStorage.getReview(optionalReview.get().getId());

        assertThat(optionalReview)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("useful", 1)
                );

        // Удаление лайка

        reviewStorage.deleteLikeDislike(optionalReview.get().getId(), 1L, true);

        optionalReview = reviewStorage.getReview(optionalReview.get().getId());

        assertThat(optionalReview)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("useful", 0)
                );
    }
}

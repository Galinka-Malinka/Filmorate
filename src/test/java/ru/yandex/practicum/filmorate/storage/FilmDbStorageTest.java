package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.SortType;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_DATA_FILE = "src" + File.separator + "main" + File.separator + "resources" +
            File.separator + "data.sql";

    @BeforeEach
    public void beforeEachTest() throws IOException {
        String sqlQuery = new String(Files.readAllBytes(Paths.get(SQL_DATA_FILE)));
        jdbcTemplate.execute(sqlQuery);
    }

    @Test
    public void testAddFilm() {
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, ""));
        genres.add(new Genre(2, ""));

        filmStorage.addDirector(Director.builder().name("Director1").build());
        filmStorage.addDirector(Director.builder().name("Director2").build());

        Set<Director> directors = new HashSet<>();
        directors.add(Director.builder().id(1L).name("Director1").build());
        directors.add(Director.builder().id(2L).name("Director2").build());

        Film newFilm = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .genres(genres)
                .directors(directors)
                .duration(60)
                .build();

        Optional<Film> optionalFilm = filmStorage.addFilm(newFilm);

        assertThat(optionalFilm)
                .isPresent();
    }

    @Test
    public void testUpdateFilm() {
        Film newFilm = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm = filmStorage.addFilm(newFilm);

        assertThat(optionalFilm)
                .isPresent();

        newFilm = optionalFilm.get();

        assertThat(newFilm.getGenres())
                .size()
                .isEqualTo(0);
        assertThat(newFilm.getDirectors())
                .size()
                .isEqualTo(0);

        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, ""));
        genres.add(new Genre(2, ""));

        filmStorage.addDirector(Director.builder().name("Director1").build());
        filmStorage.addDirector(Director.builder().name("Director2").build());

        Set<Director> directors = new HashSet<>();
        directors.add(Director.builder().id(1L).name("Director1").build());
        directors.add(Director.builder().id(2L).name("Director2").build());

        Film filmForUpdate = Film.builder()
                .id(optionalFilm.get().getId())
                .name(newFilm.getName())
                .description(newFilm.getDescription())
                .releaseDate(newFilm.getReleaseDate())
                .duration(newFilm.getDuration())
                .mpa(newFilm.getMpa())
                .genres(genres)
                .directors(directors)
                .build();

        optionalFilm = filmStorage.updateFilm(filmForUpdate);

        assertThat(optionalFilm)
                .isPresent();

        newFilm = optionalFilm.get();

        assertThat(newFilm.getGenres())
                .size()
                .isEqualTo(2);

        assertThat(newFilm.getDirectors())
                .size()
                .isEqualTo(2);
    }

    @Test
    public void testUpdateFilmWithWrongId() {
        Film newFilm = Film.builder()
                .id(555L)
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm = filmStorage.updateFilm(newFilm);

        assertThat(optionalFilm)
                .isEmpty();
    }

    @Test
    public void testGetFilm() {
        Film newFilm = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm = filmStorage.addFilm(newFilm);

        assertThat(optionalFilm)
                .isPresent();

        Optional<Film> optionalGetFilm = filmStorage.getFilm(optionalFilm.get().getId());

        assertThat(optionalGetFilm)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", optionalFilm.get().getId())
                );
    }

    @Test
    public void testAddDirector() {
        Director director = Director.builder().name("Director").build();

        Optional<Director> optionalDirector = filmStorage.addDirector(director);

        assertThat(optionalDirector)
                .isPresent()
                .hasValueSatisfying(dir ->
                        assertThat(dir).hasFieldOrPropertyWithValue("name", "Director"));
    }

    @Test
    public void testUpdateDirector() {
        Director directorForUpdate = Director.builder().name("Director").build();
        filmStorage.addDirector(directorForUpdate);

        Director director = Director.builder().id(1L).name("UpdatedDirector").build();
        Optional<Director> optionalUpdatedDirector = filmStorage.updateDirector(director);

        assertThat(optionalUpdatedDirector)
                .isPresent()
                .hasValueSatisfying(dir ->
                        assertThat(dir).hasFieldOrPropertyWithValue("name", "UpdatedDirector"));
    }

    @Test
    public void testRemoveDirector() {
        Director director = Director.builder().name("Director").build();
        Optional<Director> newDirector = filmStorage.addDirector(director);

        assertThat(newDirector).isPresent();

        filmStorage.removeDirector(newDirector.get().getId());

        Optional<Director> optionalDirector = filmStorage.getDirector(newDirector.get().getId());
        assertThat(optionalDirector).isEmpty();
    }

    @Test
    public void testGetAllDirectors() {
        Director director1 = Director.builder().name("Director1").build();
        Optional<Director> newDirector1 = filmStorage.addDirector(director1);

        Director director2 = Director.builder().name("Director2").build();
        Optional<Director> newDirector2 = filmStorage.addDirector(director2);

        Collection<Director> directors = filmStorage.getAllDirectors();
        List<Director> listDirectors = (List<Director>) directors;

        assertThat(newDirector1).isPresent();
        assertThat(newDirector2).isPresent();
        assertThat(listDirectors).isNotEmpty()
                .contains(newDirector1.get())
                .contains(newDirector2.get());
    }

    @Test
    public void testGetDirectorById() {
        Director director = Director.builder().name("Director1").build();
        Optional<Director> newDirector = filmStorage.addDirector(director);

        Optional<Director> foundDirector = filmStorage.getDirector(1L);

        assertThat(newDirector).isPresent();
        assertThat(foundDirector)
                .isPresent()
                .hasValueSatisfying(dir ->
                        assertThat(dir).hasFieldOrPropertyWithValue("name", newDirector.get().getName()));
    }

    @Test
    public void testGetDirectorFilms() {
        Director director = Director.builder().id(1L).name("Director").build();
        filmStorage.addDirector(director);

        Set<Director> directors = new HashSet<>();
        directors.add(Director.builder().id(1L).name("Director").build());

        Film newFilm = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .directors(directors)
                .build();

        Optional<Film> optionalFilm = filmStorage.addFilm(newFilm);

        Film newFilm2 = Film.builder()
                .name("Film2 name")
                .description("Film2 description")
                .releaseDate(LocalDate.of(1999, 1, 1))
                .duration(60)
                .directors(directors)
                .build();

        Optional<Film> optionalFilm2 = filmStorage.addFilm(newFilm2);

        Collection<Film> films = filmStorage.getDirectorFilms(1L, SortType.YEAR);

        assertThat(optionalFilm).isPresent();
        assertThat(optionalFilm2).isPresent();
        assertThat(films).size().isEqualTo(2);
        assertThat(films)
                .contains(optionalFilm.get())
                .contains(optionalFilm2.get());
        assertThat(films.stream().findFirst()).isEqualTo(optionalFilm2);

        User newUser = User.builder()
                .login("NewUser")
                .email("user email")
                .name("New user")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> optionalUser = userStorage.addUser(newUser);

        User newUser2 = User.builder()
                .login("NewUser2")
                .email("user2 email")
                .name("New user2")
                .birthday(LocalDate.of(2002, 1, 1))
                .build();

        Optional<User> optionalUser2 = userStorage.addUser(newUser2);

        assertThat(optionalUser).isPresent();
        assertThat(optionalUser2).isPresent();

        filmStorage.addLike(optionalFilm.get().getId(), optionalUser.get().getId(), 10);
        filmStorage.addLike(optionalFilm2.get().getId(), optionalUser.get().getId(), 10);
        filmStorage.addLike(optionalFilm2.get().getId(), optionalUser2.get().getId(), 10);

        Collection<Film> filmsSortedByLikes = filmStorage.getDirectorFilms(1L, SortType.LIKES);

        assertThat(filmsSortedByLikes).size().isEqualTo(2);
        assertThat(filmsSortedByLikes)
                .contains(optionalFilm.get())
                .contains(optionalFilm2.get());
        assertThat(films.stream().findFirst()).isEqualTo(optionalFilm2);
    }

    @Test
    public void testGetFilmWithWrongId() {
        Optional<Film> optionalFilm = filmStorage.getFilm(555L);

        assertThat(optionalFilm)
                .isEmpty();
    }

    @Test
    public void testGetFilms() {
        Film newFilm = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm = filmStorage.addFilm(newFilm);

        assertThat(optionalFilm)
                .isPresent();

        Collection<Film> films = filmStorage.getFilms();
        assertThat(films)
                .isNotEmpty();
    }

    @Test
    public void testAddLike() {
        Film newFilm = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm = filmStorage.addFilm(newFilm);

        assertThat(optionalFilm)
                .isPresent();

        newFilm = optionalFilm.get();

        assertThat(newFilm.getLikes())
                .isNullOrEmpty();

        User newUser = User.builder()
                .login("NewUser")
                .email("user email")
                .name("New user")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> optionalUser = userStorage.addUser(newUser);

        assertThat(optionalUser)
                .isPresent();

        filmStorage.addLike(newFilm.getId(), optionalUser.get().getId(), 10);

        optionalFilm = filmStorage.getFilm(newFilm.getId());

        assertThat(optionalFilm)
                .isPresent();

        newFilm = optionalFilm.get();

        assertThat(newFilm.getLikes())
                .size()
                .isEqualTo(1);

        filmStorage.removeLike(newFilm.getId(), optionalUser.get().getId());
    }

    @Test
    public void testRemoveLike() {
        Film newFilm = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm = filmStorage.addFilm(newFilm);

        assertThat(optionalFilm)
                .isPresent();

        User newUser = User.builder()
                .login("NewUser")
                .email("user email")
                .name("New user")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> optionalUser = userStorage.addUser(newUser);

        assertThat(optionalUser)
                .isPresent();

        filmStorage.addLike(optionalFilm.get().getId(), optionalUser.get().getId(), 10);

        optionalFilm = filmStorage.getFilm(optionalFilm.get().getId());

        assertThat(optionalFilm)
                .isPresent();

        assertThat(optionalFilm.get().getLikes())
                .size()
                .isEqualTo(1);

        filmStorage.removeLike(optionalFilm.get().getId(), optionalUser.get().getId());

        optionalFilm = filmStorage.getFilm(optionalFilm.get().getId());

        assertThat(optionalFilm)
                .isPresent();

        assertThat(optionalFilm.get().getLikes())
                .isNullOrEmpty();
    }

    @Test
    public void testGetPopularFilms() {
        Film newFilm1 = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm1 = filmStorage.addFilm(newFilm1);

        assertThat(optionalFilm1)
                .isPresent();

        Film newFilm2 = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm2 = filmStorage.addFilm(newFilm2);

        assertThat(optionalFilm2)
                .isPresent();

        User newUser1 = User.builder()
                .login("NewUser")
                .email("user email")
                .name("New user")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> userOptional1 = userStorage.addUser(newUser1);

        assertThat(userOptional1)
                .isPresent();

        User newUser2 = User.builder()
                .login("NewUser")
                .email("user email")
                .name("New user")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> userOptional2 = userStorage.addUser(newUser2);

        assertThat(userOptional2)
                .isPresent();

        filmStorage.addLike(optionalFilm1.get().getId(), userOptional1.get().getId(), 10);
        filmStorage.addLike(optionalFilm1.get().getId(), userOptional2.get().getId(), 10);
        filmStorage.addLike(optionalFilm2.get().getId(), userOptional1.get().getId(), 10);

        Collection<Film> films = filmStorage.getPopularFilms(2, 0, 0);
        List<Film> listFilms = (List<Film>) films;

        assertThat(films)
                .isNotEmpty()
                .size()
                .isEqualTo(2);

        assertThat(listFilms.get(0))
                .hasFieldOrPropertyWithValue("id", optionalFilm1.get().getId());
        assertThat(listFilms.get(1))
                .hasFieldOrPropertyWithValue("id", optionalFilm2.get().getId());
    }


    @Test
    public void shouldGetFilmsWithTenPointRatingSystem() {
        Film newFilm1 = Film.builder()
                .name("Film1 name")
                .description("Film1 description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> film1 = filmStorage.addFilm(newFilm1);

        assertThat(film1)
                .isPresent();

        Film newFilm2 = Film.builder()
                .name("Film2 name")
                .description("Film2 description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> film2 = filmStorage.addFilm(newFilm2);

        assertThat(film2)
                .isPresent();

        Film newFilm3 = Film.builder()
                .name("Film3 name")
                .description("Film3 description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> film3 = filmStorage.addFilm(newFilm3);

        assertThat(film3)
                .isPresent();

        User newUser1 = User.builder()
                .login("NewUser1")
                .email("user1 email")
                .name("New user1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> userOptional1 = userStorage.addUser(newUser1);

        assertThat(userOptional1)
                .isPresent();

        User newUser2 = User.builder()
                .login("NewUser2")
                .email("user2 email")
                .name("New user2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> userOptional2 = userStorage.addUser(newUser2);

        assertThat(userOptional2)
                .isPresent();

        User newUser3 = User.builder()
                .login("NewUser3")
                .email("user3 email")
                .name("New user3")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> userOptional3 = userStorage.addUser(newUser3);

        assertThat(userOptional3)
                .isPresent();

        filmStorage.addLike(film1.get().getId(), userOptional1.get().getId(), 4);
        filmStorage.addLike(film1.get().getId(), userOptional2.get().getId(), 5);
        filmStorage.addLike(film1.get().getId(), userOptional3.get().getId(), 3);
        filmStorage.addLike(film2.get().getId(), userOptional1.get().getId(), 10);
        filmStorage.addLike(film2.get().getId(), userOptional2.get().getId(), 10);
        filmStorage.addLike(film2.get().getId(), userOptional3.get().getId(), 10);
        filmStorage.addLike(film3.get().getId(), userOptional1.get().getId(), 10);
        filmStorage.addLike(film3.get().getId(), userOptional2.get().getId(), 10);

        String sqlQuery = "select distinct f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id," +
                "  mpa.name as mpa_name, mpa.description as mpa_description," +
                " count(l.user_id) as count_users, avg(l.mark) as avg_likes" +
                " from films as f left join mpa on f.mpa_id = mpa.mpa_id" +
                " left join film_likes as l on f.film_id = l.film_id" +
                " where " +
                "f.film_id in ( select top 3 films.film_id" +
                " from films" +
                " left join film_likes on films.film_id = film_likes.film_id" +
                " group by films.film_id" +
                " order by avg(film_likes.mark) desc," +
                " count(film_likes.user_id) desc) " +
                " group by f.film_id" +
                " order by avg_likes desc, count_users desc";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        System.out.println(films);
        assertThat(films).size().isEqualTo(3);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", film2.get().getId());
        assertThat(films.get(1)).hasFieldOrPropertyWithValue("id", film3.get().getId());
        assertThat(films.get(2)).hasFieldOrPropertyWithValue("id", film1.get().getId());
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .mpa(new Mpa(resultSet.getInt("mpa_id"),
                        resultSet.getString("mpa_name"),
                        resultSet.getString("mpa_description")))
                .genres(filmStorage.getFilmGenres(resultSet.getLong("film_id")))
                .directors(filmStorage.getFilmDirectors(resultSet.getLong("film_id")))
                .likes(filmStorage.getFilmLikes(resultSet.getLong("film_id")))
                .build();
    }

    @Test
    public void testGetAllMpa() {
        Collection<Mpa> mpa = filmStorage.getAllMpa();
        assertThat(mpa)
                .size()
                .isEqualTo(5);
    }

    @Test
    public void testGetMpaById() {
        Optional<Mpa> mpa = filmStorage.getMpaById(1);
        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(mpa1 ->
                        assertThat(mpa1).hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    public void testGetMpaByWrongId() {
        Optional<Mpa> mpa = filmStorage.getMpaById(555);
        assertThat(mpa)
                .isEmpty();
    }

    @Test
    public void testGetAllGenres() {
        Collection<Genre> genres = filmStorage.getAllGenres();
        assertThat(genres)
                .size()
                .isEqualTo(6);
    }

    @Test
    public void testGetGenreById() {
        Optional<Genre> genre = filmStorage.getGenreById(1);
        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(genre1 ->
                        assertThat(genre1).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void testGetGenreByWrongId() {
        Optional<Genre> genre = filmStorage.getGenreById(555);
        assertThat(genre)
                .isEmpty();
    }

    @Test
    public void testGetFilmsRecommendation() {
        //Film 1
        Film newFilm1 = Film.builder()
                .name("Film 1")
                .description("Film 1 description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm1 = filmStorage.addFilm(newFilm1);

        assertThat(optionalFilm1)
                .isPresent();

        //Film 2
        Film newFilm2 = Film.builder()
                .name("Film 2")
                .description("Film 2 description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm2 = filmStorage.addFilm(newFilm2);

        assertThat(optionalFilm2)
                .isPresent();

        //Film 3
        Film newFilm3 = Film.builder()
                .name("Film 3")
                .description("Film 3 description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(60)
                .build();

        Optional<Film> optionalFilm3 = filmStorage.addFilm(newFilm3);

        assertThat(optionalFilm3)
                .isPresent();

        //User 1
        User newUser1 = User.builder()
                .login("NewUser1")
                .email("user 1 email")
                .name("New user 1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> optionalUser1 = userStorage.addUser(newUser1);

        assertThat(optionalUser1)
                .isPresent();

        //User 2
        User newUser2 = User.builder()
                .login("NewUser2")
                .email("user 2 email")
                .name("New user 2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> optionalUser2 = userStorage.addUser(newUser2);

        assertThat(optionalUser2)
                .isPresent();

        //User 3
        User newUser3 = User.builder()
                .login("NewUser3")
                .email("user 3 email")
                .name("New user 3")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Optional<User> optionalUser3 = userStorage.addUser(newUser3);

        assertThat(optionalUser3)
                .isPresent();

        //Add likes
        filmStorage.addLike(optionalFilm1.get().getId(), optionalUser1.get().getId(), 10);
        filmStorage.addLike(optionalFilm1.get().getId(), optionalUser2.get().getId(), 10);
        filmStorage.addLike(optionalFilm2.get().getId(), optionalUser2.get().getId(), 10);
        filmStorage.addLike(optionalFilm3.get().getId(), optionalUser3.get().getId(), 10);

        //Get recommendation
        Collection<Film> films = filmStorage.getFilmsRecommendation(optionalUser1.get().getId());
        List<Film> listFilms = (List<Film>) films;

        assertThat(films)
                .isNotEmpty()
                .size()
                .isEqualTo(1);

        assertThat(listFilms.get(0))
                .hasFieldOrPropertyWithValue("id", optionalFilm2.get().getId());
    }
}

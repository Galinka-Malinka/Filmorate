package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventManager;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventManager eventManager;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("eventManager") EventManager eventManager) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventManager = eventManager;
    }

    public Optional<User> addUser(User user) {
        return userStorage.addUser(user);
    }

    public Optional<User> updateUser(User user) {
        if (userStorage.userNotExist(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
        return userStorage.updateUser(user);
    }

    public Optional<User> getUser(Long id) {
        Optional<User> foundUser = userStorage.getUser(id);
        if (userStorage.userNotExist(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        return foundUser;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        if (userStorage.userNotExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        if (userStorage.userNotExist(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден.");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userStorage.userNotExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        if (userStorage.userNotExist(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден.");
        }
        userStorage.removeFriend(userId, friendId);
    }

    public void confirmFriend(Long userId, Long friendId) {
        if (userStorage.userNotExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        if (userStorage.userNotExist(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден.");
        }
        userStorage.confirmFriend(userId, friendId);
    }

    public Collection<User> getFriends(Long id) {
        if (userStorage.userNotExist(id)) {
            throw new NotFoundException("Нет такого пользователя");
        }
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

    public void deleteUserById(Long id) {
        if (userStorage.userNotExist(id)) {
            throw new NotFoundException("Нет такого пользователя");
        }
        userStorage.deleteUserById(id);
    }

    public Collection<Film> getFilmsRecommendation(long userId) {
        if (userStorage.userNotExist(userId)) {
            throw new NotFoundException("Нет такого пользователя");
        }
        return filmStorage.getFilmsRecommendation(userId);
    }

    public Collection<Event> getEvents(Long id) {
        if (userStorage.userNotExist(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        return eventManager.getEvents(id);
    }
}

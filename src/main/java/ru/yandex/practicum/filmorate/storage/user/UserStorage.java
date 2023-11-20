package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Optional<User> addUser(User user);

    Optional<User> updateUser(User user) throws ValidationException;

    Optional<User> getUser(Long id) throws ValidationException;

    Collection<User> getUsers();

    boolean userExist(Long id);

    boolean userNotExist(Long id);

    void addFriend(Long userId, Long friendId) throws ValidationException;

    void removeFriend(Long userId, Long friendId) throws ValidationException;

    void confirmFriend(Long id, Long friendId) throws ValidationException;

    Collection<User> getFriends(Long id) throws ValidationException;

    Collection<User> getCommonFriends(Long id, Long otherId) throws ValidationException;

    void deleteUserById(Long id);
}

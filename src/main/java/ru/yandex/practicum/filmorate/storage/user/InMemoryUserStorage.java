package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long lastId = 0;

    @Override
    public Optional<User> addUser(User user) {
        String userName = user.getName();
        if (!StringUtils.hasText(userName)) {
            userName = user.getLogin();
        }

        User newUser = User.builder()
                .id(++lastId)
                .login(user.getLogin())
                .email(user.getEmail())
                .name(userName)
                .birthday(user.getBirthday())
                .friends(new HashSet<>())
                .build();

        log.info("Добавлен пользователь: " + user.getLogin());
        users.put(newUser.getId(), newUser);
        return Optional.of(newUser);
    }

    @Override
    public Optional<User> updateUser(User user) throws ValidationException {
        Optional<User> foundUser = getUserById(user.getId());
        if (foundUser.isEmpty()) {
            return foundUser;
        }

        User newUser = User.builder()
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friends(foundUser.get().getFriends())
                .build();

        log.info("Обновлен пользователь: " + newUser.getName());
        users.put(newUser.getId(), newUser);
        return Optional.of(newUser);
    }

    @Override
    public Optional<User> getUser(Long id) throws ValidationException {
        return getUserById(id);
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public boolean userExist(Long id) {
        return users.containsKey(id);
    }

    @Override
    public boolean userNotExist(Long id) {
        return !users.containsKey(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) throws ValidationException {
        Optional<User> userOptional = getUserById(userId);
        Optional<User> friendOptional = getUserById(friendId);
        userOptional.ifPresent(user -> user.addFriend(friendId));
        friendOptional.ifPresent(user -> user.addFriend(userId));
        log.info("Пользователю с id " + userId + " добавлен друг с id " + friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) throws ValidationException {
        Optional<User> userOptional = getUserById(userId);
        Optional<User> friendOptional = getUserById(friendId);
        userOptional.ifPresent(user -> user.removeFriend(friendId));
        friendOptional.ifPresent(user -> user.removeFriend(userId));
        log.info("У пользователю с id " + userId + " удален друг с id " + friendId);
    }

    @Override
    public Collection<User> getFriends(Long id) throws ValidationException {
        Set<Long> friendsId = getUserById(id).get().getFriends();
        return getUsersByIds(friendsId);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) throws ValidationException {
        Set<Long> friendsId = new HashSet<>(getUserById(id).get().getFriends());
        Set<Long> otherFriendsId = getUserById(otherId).get().getFriends();

        friendsId.retainAll(otherFriendsId);
        return getUsersByIds(friendsId);
    }

    @Override
    public void deleteUserById(Long id) {
    }

    @Override
    public void confirmFriend(Long id, Long friendId) throws ValidationException {

    }

    private Optional<User> getUserById(Long id) throws ValidationException {
        if (!users.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(users.get(id));
    }

    private Collection<User> getUsersByIds(Set<Long> usersId) {
        Collection<User> userList = new ArrayList<>();
        usersId.forEach(id -> userList.add(users.get(id)));
        return userList;
    }
}

package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserTest {

    @Autowired
    ValidatingService service;

    @Test
    public void createInvalidUsersTest() {
        Collection<User> users = new ArrayList<>();

        //Нет адреса почты
        users.add(User.builder()
                .name("Name")
                .login("Login")
                .birthday(LocalDate.of(1000,1,1))
                .build());

        //Неправильный адреса почты
        users.add(User.builder()
                .email("11111")
                .name("Name")
                .login("Login")
                .birthday(LocalDate.of(1000,1,1))
                .build());

        //Нет логина
        users.add(User.builder()
                .email("1@1.ru")
                .name("Name")
                .birthday(LocalDate.of(1000,1,1))
                .build());

        //Логин с пробелами
        users.add(User.builder()
                .email("1@1.ru")
                .name("Name")
                .login("Log in")
                .birthday(LocalDate.of(1000,1,1))
                .build());

        //Пустой пробелами
        users.add(User.builder()
                .email("1@1.ru")
                .name("Name")
                .login("")
                .birthday(LocalDate.of(1000,1,1))
                .build());

        //Нет даты рождения
        users.add(User.builder()
                .email("1@1.ru")
                .name("Name")
                .login("Log in")
                .build());

        //Дата рождения в будущем
        users.add(User.builder()
                .email("1@1.ru")
                .name("Name")
                .login("Log in")
                .birthday(LocalDate.of(3000,1,1))
                .build());

        users.forEach(x -> assertThrows(ValidationException.class, () -> service.validateSimpleUser(x)));
    }
}

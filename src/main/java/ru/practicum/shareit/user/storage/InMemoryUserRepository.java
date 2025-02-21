package ru.practicum.shareit.user.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public User saveUser(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        log.debug("Добавлен юзер с Id {}", user.getId());
        return user;
    }

    @Override
    public Optional<List<User>> findAll() {
        if (users.size() == 0) {
            log.error("Ошибка при получении списка юзеров");
            return Optional.empty();
        }
        List<User> resultList = new ArrayList<>(users.values());
        return Optional.of(resultList);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }
        log.error("Ошибка при получении юзера с ID" + id);
        return Optional.empty();
    }

    @Override
    public User update(User newUser) {
        User oldUser = users.get(newUser.getId());
        if (newUser.getEmail() != null && !newUser.getEmail().isEmpty()) {
            log.trace("Изменен имейл юзера с Id {}", newUser.getId());
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null && !newUser.getName().isEmpty()) {
            log.trace("Изменено имя юзера с Id {}", newUser.getId());
            oldUser.setName(newUser.getName());
        }
        log.debug("Обновлен юзер с Id {}", newUser.getId());
        return oldUser;
    }

    @Override
    public boolean isUserIdExists(Long id) {
        return users.containsKey(id);
    }

    @Override
    public void remove(Long id) {
        users.remove(id);
        log.debug("Пользователь с id " + id + " удален.");
    }

    private long getId() {
        long lastId = users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
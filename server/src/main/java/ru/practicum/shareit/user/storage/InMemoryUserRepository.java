package ru.practicum.shareit.user.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.expection.InternalServerException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository("inMemoryRepository")
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public User saveUser(User user) {
        user.setId(getId());
        if (existsByEmail(user)) {
            throw new InternalServerException("Этот имейл уже используется");
        }
        users.put(user.getId(), user);
        log.debug("Добавлен юзер с Id {}", user.getId());
        return user;
    }

    @Override
    public List<User> findAll() {
        if (users.isEmpty()) {
            log.error("Список юзеров пуст.");
            return Collections.emptyList();
        }
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.of(users.get(id));
    }

    @Override
    public User update(User newUser) {
        if (existsByEmail(newUser)) {
            throw new InternalServerException("Этот имейл уже используется");
        }
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

    @Override
    public boolean existsByEmail(User user) {
        return findAll().stream()
                        .anyMatch(u -> u.getEmail().equals(user.getEmail()));
    }

    private long getId() {
        long lastId = users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
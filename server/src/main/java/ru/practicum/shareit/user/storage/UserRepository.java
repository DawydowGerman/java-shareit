package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User saveUser(User user);

    Optional<List<User>> findAll();

    boolean isUserIdExists(Long id);

    User update(User newUser);

    Optional<User> getUserById(Long id);

    void remove(Long id);
}
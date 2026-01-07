package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository {
    Request save(Request request);

    List<Request> findAll();

    Request update(Request newRequest);

    Optional<Request> getUserById(Long id);

    void remove(Long id);
}

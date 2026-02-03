package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository {
    Request save(Request request);

    List<Request> findAll();

    Optional<Request> getRequestById(Long id);

    List<Request> getRequestsByAuthorId(Long authorId);

    Request update(Request newRequest);

    boolean isRequestIdExists(Long id);

    void deleteById(Long id);
}

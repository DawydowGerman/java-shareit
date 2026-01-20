package ru.practicum.shareit.request.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.expection.InternalServerException;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.model.Request;

import java.util.*;

@Repository("inMemoryRepository")
public class InMemoryRequestRepository {
    private final Map<Long, Request> requests = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(RequestController.class);

    public Request save(Request request) {
        request.setId(getId());
        requests.put(request.getId(), request);
        log.debug("Добавлен реквест с Id {}", request.getId());
        return request;
    }

    public List<Request> findAll() {
        if (requests.isEmpty()) {
            log.error("Список реквестов пуст.");
            return Collections.emptyList();
        }
        return new ArrayList<>(requests.values());
    }

    public Request update(Request newRequest) {
        if (!requests.containsKey(newRequest.getId())) {
            throw new NotFoundException("Запрос с id " + newRequest.getId() + " отсутствует.");
        }
        Request oldRequest = requests.get(newRequest.getId());
        if (newRequest.getDescription() != null && !newRequest.getDescription().equals(oldRequest.getDescription())) {
            oldRequest.setDescription(newRequest.getDescription());
        }
        log.debug("Обновлен запрос с Id {}", newRequest.getId());
        return oldRequest;
    }


    private long getId() {
        long lastId = requests.values().stream()
                .mapToLong(Request::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }

}

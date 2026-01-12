package ru.practicum.shareit.request.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.model.Request;

import java.util.HashMap;
import java.util.Map;

@Repository("inMemoryRepository")
public class InMemoryRequestRepository {
    private final Map<Long, Request> requests = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(RequestController.class);


}

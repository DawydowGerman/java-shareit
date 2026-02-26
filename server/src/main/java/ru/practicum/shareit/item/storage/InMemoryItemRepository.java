package ru.practicum.shareit.item.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

public class InMemoryItemRepository {
    private final Map<Long, Item> requests = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    public Item save(Item item) {
        item.setId(getId());
        requests.put(item.getId(), item);
        log.debug("Добавлена dtom с Id {}", item.getId());
        return item;
    }

    private long getId() {
        long lastId = requests.values().stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}

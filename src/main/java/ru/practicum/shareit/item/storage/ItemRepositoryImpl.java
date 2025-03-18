package ru.practicum.shareit.item.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.controller.UserController;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public Item save(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        log.debug("Добавлена вещь с Id {}", item.getId());
        return item;
    }

    @Override
    public Optional<List<Item>> findAll() {
        if (items.size() == 0) {
            log.error("Ошибка при получении списка вещей");
            return Optional.empty();
        }
        List<Item> resultList = new ArrayList<>(items.values());
        return Optional.of(resultList);
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id));
        }
        log.error("Ошибка при получении вещи с ID" + id);
        return Optional.empty();
    }

    @Override
    public Optional<List<Item>> getItemsByUserid(Long userId) {
        List<Item> resultList = items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
        if (resultList.size() > 0) {
            return Optional.of(resultList);
        }
        log.error("У юзера c Id " + userId + " отсутствуют вещи.");
        return Optional.empty();
    }

    @Override
    public Optional<List<Item>> getItemsByText(String text) {
        List<Item> resultList = items.values()
                .stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase())
                        || item.getName().toLowerCase().contains(text.toLowerCase())
                        && item.getAvailable().booleanValue())
                .collect(Collectors.toList());
        if (resultList.size() > 0) {
            return Optional.of(resultList);
        }
        List<Item> emtpytList = new ArrayList<>();
        return Optional.of(emtpytList);
    }

    @Override
    public Item update(Item newItem, Long itemId) {
        Item oldItem = items.get(itemId);
        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            log.trace("Изменено наименование вещи с Id {}", newItem.getId());
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
            log.trace("Изменено описание вещи с Id {}", newItem.getId());
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            log.trace("Изменено описание вещи с Id {}", newItem.getId());
            oldItem.setAvailable(newItem.getAvailable());
        }
        log.debug("Обновлена вещь с Id {}", newItem.getId());
        return oldItem;
    }

    @Override
    public boolean isItemIdExists(Long id) {
        return items.containsKey(id);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        items.values().removeIf(item -> item.getId().equals(itemId) && item.getOwner().getId().equals(userId));
    }

    private long getId() {
        long lastId = items.values().stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}

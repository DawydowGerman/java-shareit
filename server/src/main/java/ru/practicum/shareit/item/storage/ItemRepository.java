package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<List<Item>> findAll();

    Optional<Item> getItemById(Long id);

    Optional<List<Item>> getItemsByUserid(Long userId);

    Optional<List<Item>> getItemsByText(String text);

    void deleteByUserIdAndItemId(long userId, long itemId);

    Item update(Item newItem, Long itemId);

    boolean isItemIdExists(Long id);
}
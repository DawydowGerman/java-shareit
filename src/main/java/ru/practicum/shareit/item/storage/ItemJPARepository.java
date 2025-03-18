package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemJPARepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long ownerId);

    @Query("select it " +
            "from Item as it " +
            "where lower(it.name) like lower(concat('%', ?1,'%')) " +
            " or lower(it.description) like lower(concat('%', ?1,'%'))")
    List<Item> getItemsByText(String text);
}
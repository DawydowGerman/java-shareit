package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemJPARepository extends JpaRepository<Item, Long> {
    Optional<List<Item>> findByOwnerId(long ownerId);

    @Query("select it " +
            "from Item as it " +
            "where lower(it.name) like lower(concat('%', ?1,'%')) " +
            " or lower(it.description) like lower(concat('%', ?1,'%'))")
    List<Item> getItemsByText(String text);

    @Query(value = "SELECT * FROM items WHERE request_id IN (:request_id)"
            , nativeQuery = true)
    Optional<List<Item>> getItemsByRequest(@Param("request_id") List<Long> request_id);
}
package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentJPARepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findByItemId(Long itemId);
}
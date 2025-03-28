package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentJPARepository extends JpaRepository<Comment, Long>
        , JpaSpecificationExecutor<Comment> {
    Optional<List<Comment>> findByItemId(Long itemId);
}
package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.domain.Specification;

public class CommentSpecification {
    public static Specification<Comment> hasItem(Item item) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("item"), item);
    }
}
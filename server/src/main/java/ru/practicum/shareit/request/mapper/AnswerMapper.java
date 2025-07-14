package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.AnswerToRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerMapper {
    public static AnswerToRequest toAnswer(Item item) {
        return new AnswerToRequest(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getOwner().getId()
                );
    }
}
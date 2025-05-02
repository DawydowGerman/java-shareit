package ru.practicum.shareit.request.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnswerToRequest {
    private Long itemId;
    private String name;
    private String description;
    private Long ownerId;
}
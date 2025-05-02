package ru.practicum.shareit.item.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentRequestDTO {
    private String text;
}
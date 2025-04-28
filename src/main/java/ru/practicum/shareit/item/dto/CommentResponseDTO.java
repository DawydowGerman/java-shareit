package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentResponseDTO {
    @NotNull
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
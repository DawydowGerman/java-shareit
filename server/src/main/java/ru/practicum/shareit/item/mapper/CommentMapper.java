package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.model.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment toModel(CommentRequestDTO commentRequestDTO) {
        return new Comment(
                commentRequestDTO.getText()
        );
    }

    public static CommentResponseDTO toDto(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
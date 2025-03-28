package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemResponseDTO {
    @NotNull
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long requestId;
    private BookingResponseDTO lastBooking;
    private BookingResponseDTO nextBooking;
    private List<Comment> comments;

    public ItemResponseDTO(Long id, String name,
                           String description, Boolean available,
                           User owner, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.requestId = requestId;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }
}
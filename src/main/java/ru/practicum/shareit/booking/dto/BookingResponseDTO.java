package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingResponseDTO {
    @NotNull
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;
}
package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemResponseWithCommentsDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingResponseDTO lastBooking;
    private BookingResponseDTO nextBooking;
    private List<String> comments;

    public ItemResponseWithCommentsDTO(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private Long ownerId;
    private String request;
}
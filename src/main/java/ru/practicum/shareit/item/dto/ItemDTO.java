package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private String request;

    public ItemDTO(Long id, String name, String description,
                   Boolean available, Long ownerId, String request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId = ownerId;
        this.request = request;
    }
}
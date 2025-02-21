package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static Item toModel(ItemDTO itemDTO) {
        return new Item(
                itemDTO.getId(),
                itemDTO.getName(),
                itemDTO.getDescription(),
                itemDTO.getAvailable(),
                itemDTO.getOwnerId(),
                itemDTO.getRequest()
        );
    }

    public static ItemDTO toDto(Item item) {
        return new ItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequest()
        );
    }
}

package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item toModel(ItemRequestDTO itemRequestDTO) {
        return new Item(
                itemRequestDTO.getName(),
                itemRequestDTO.getDescription(),
                itemRequestDTO.getAvailable(),
                itemRequestDTO.getOwnerId(),
                itemRequestDTO.getRequest()
        );
    }

    public static ItemResponseDTO toDto(Item item) {
        return new ItemResponseDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequest()
        );
    }
}
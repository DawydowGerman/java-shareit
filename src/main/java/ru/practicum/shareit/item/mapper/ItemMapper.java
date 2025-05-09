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
                itemRequestDTO.getOwner()
        );
    }

    public static Item toModelFromRespDTO(ItemResponseDTO itemResponseDTO) {
        return new Item(
                itemResponseDTO.getId(),
                itemResponseDTO.getName(),
                itemResponseDTO.getDescription(),
                itemResponseDTO.getAvailable(),
                itemResponseDTO.getOwner(),
                itemResponseDTO.getRequestId()
        );
    }

    public static ItemResponseDTO toDto(Item item) {
        return new ItemResponseDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId()
        );
    }
}
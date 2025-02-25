package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;

import java.util.List;

public interface ItemService {
    ItemResponseDTO addNewItem(Long userId, ItemRequestDTO itemRequestDTO);

    List<ItemResponseDTO> getItemsByUserid(Long userId);

    ItemResponseDTO getItemById(Long userId, Long itemId);

    List<ItemResponseDTO> getItemsByText(String queryParam, Long userId);

    ItemResponseDTO update(Long userId, Long itemId, ItemRequestDTO itemRequestDTO);

    void deleteItem(long userId, long itemId);
}
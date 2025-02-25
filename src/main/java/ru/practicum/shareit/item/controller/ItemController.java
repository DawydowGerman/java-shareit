package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDTO addNewItem(@PositiveOrZero @RequestHeader(SHARER_USER_ID) Long userId,
                                      @Valid @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("Item's addition: {}", itemRequestDTO);
        return itemService.addNewItem(userId, itemRequestDTO);
    }

    @GetMapping
    public List<ItemResponseDTO> getItemsByUserid(@PositiveOrZero @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("Request things by user ID: {}", userId);
        return itemService.getItemsByUserid(userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDTO getItemById(@PositiveOrZero @RequestHeader(SHARER_USER_ID) Long userId,
                               @PathVariable(name = "itemId") Long itemId) {
        log.info("Request things by item ID: {}", itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDTO> getItemsByText(@PositiveOrZero @RequestHeader(value = SHARER_USER_ID, required = false) Long userId,
                                        @RequestParam(required = false) String text) {
        log.info("Request things by text query: {}", text);
        return itemService.getItemsByText(text, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDTO update(@PositiveOrZero @RequestHeader(SHARER_USER_ID) Long userId,
                                  @PositiveOrZero @PathVariable(name = "itemId") Long itemId,
                          @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("Item's update: {}", itemRequestDTO);
        return itemService.update(userId, itemId, itemRequestDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PositiveOrZero @RequestHeader("X-Later-User-Id") long userId,
                           @PositiveOrZero @PathVariable(name = "itemId") long itemId) {
        log.info("item's removal by ID: {}", itemId);
        itemService.deleteItem(userId, itemId);
    }
}
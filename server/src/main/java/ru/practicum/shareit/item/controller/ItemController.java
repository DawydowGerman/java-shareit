package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
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
    public ItemResponseDTO addNewItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                      @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("Item's addition: {}", itemRequestDTO);
        return itemService.addNewItem(userId, itemRequestDTO);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDTO addNewComment(@RequestHeader(SHARER_USER_ID) Long userId,
                                            @PathVariable(name = "itemId") Long itemId,
                                            @RequestBody CommentRequestDTO commentRequestDTO) {
        log.info("item's comment: {}", commentRequestDTO);
        return itemService.addNewComment(userId, itemId, commentRequestDTO);
    }

    @GetMapping
    public List<ItemResponseDTO> getItemsByUserid(@RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("Request things by user ID: {}", userId);
        return itemService.getItemsByUserid(userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDTO getItemById(@RequestHeader(SHARER_USER_ID) Long userId,
                                       @PathVariable(name = "itemId") Long itemId) {
        log.info("Request things by item ID: {}", itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDTO> getItemsByText(@RequestHeader(value = SHARER_USER_ID, required = false) Long userId,
                                                @RequestParam(required = false) String text) {
        log.info("Request things by text query: {}", text);
        return itemService.getItemsByText(text, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDTO update(@RequestHeader(SHARER_USER_ID) Long userId,
                                  @PathVariable(name = "itemId") Long itemId,
                                  @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("Item's update: {}", itemRequestDTO);
        return itemService.update(userId, itemId, itemRequestDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(SHARER_USER_ID) long userId,
                           @PathVariable(name = "itemId") long itemId) {
        log.info("item's removal by ID: {}", itemId);
        itemService.deleteItem(userId, itemId);
    }
}
package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid ItemRequestDTO itemRequestDTO) {
        log.info("Creating booking {}, userId={}", itemRequestDTO, userId);
        return itemClient.addNewItem(userId, itemRequestDTO);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable(name = "itemId") Long itemId,
                                                @RequestBody CommentRequestDTO commentRequestDTO) {
        log.info("item's comment: {}", commentRequestDTO);
        return itemClient.addNewComment(userId, itemId, commentRequestDTO);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserid(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request things by user ID: {}", userId);
        return itemClient.getItemsByUserid(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable(name = "itemId") Long itemId) {
        log.info("Request things by item ID: {}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                 @RequestParam(required = false) String text) {
        log.info("Request things by text query: {}", text);
        return itemClient.getItemsByText(text, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable(name = "itemId") Long itemId,
                                         @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("Item's update: {}", itemRequestDTO);
        return itemClient.update(userId, itemId, itemRequestDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable(name = "itemId") long itemId) {
        log.info("item's removal by ID: {}", itemId);
        itemClient.delete(userId, itemId);
    }
}
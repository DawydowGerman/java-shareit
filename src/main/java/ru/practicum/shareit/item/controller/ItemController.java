package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    @PostMapping
    public ItemDTO addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody ItemDTO itemDTO) {
        return itemService.addNewItem(userId, itemDTO);
    }

    @GetMapping
    public List<ItemDTO> getItemsByUserid(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUserid(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDTO getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable(name = "itemId") Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDTO> getItemsByText(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                         //               @RequestHeader(value = "text", required = false) String textHeader,
                                        @RequestParam(required = false) String text) {
        return itemService.getItemsByText(text, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable(name = "itemId") Long itemId,
                          @RequestBody ItemDTO itemDTO) {
        return itemService.update(userId, itemId, itemDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") long userId,
                           @PathVariable(name = "itemId") long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
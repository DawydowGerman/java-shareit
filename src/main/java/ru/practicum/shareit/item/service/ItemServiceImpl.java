package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemResponseDTO addNewItem(Long userId, ItemRequestDTO itemRequestDTO) {
        if (!userRepository.isUserIdExists(userId)) {
            throw new NotFoundException("Юзер отсутствуют");
        }
        itemRequestDTO.setOwnerId(userId);
        Item item = ItemMapper.toModel(itemRequestDTO);
        item = itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    public List<ItemResponseDTO> getItemsByUserid(Long userId) {
        if (userRepository.isUserIdExists(userId) &&
                itemRepository.getItemsByUserid(userId).isPresent()) {
            return itemRepository.getItemsByUserid(userId).get()
                    .stream()
                    .map(item -> ItemMapper.toDto(item))
                    .collect(Collectors.toList());
        }
        throw new NotFoundException("У юзера c Id " + userId + " отсутствуют вещи.");
    }

    public ItemResponseDTO getItemById(Long userId, Long itemId) {
        if (userRepository.isUserIdExists(userId) && itemRepository.isItemIdExists(itemId)) {
            return ItemMapper.toDto(itemRepository.getItemById(itemId).get());
        } else {
            throw new NotFoundException("Юзер и/или вещь отсутствуют.");
        }
    }

    public List<ItemResponseDTO> getItemsByText(String queryParam, Long userId) {
        if (queryParam.isBlank()) {
            List<ItemResponseDTO> emtpytList = new ArrayList<>();
            return emtpytList;
        }
        if (userRepository.isUserIdExists(userId)) {
            return itemRepository.getItemsByText(queryParam).get()
                    .stream()
                    .map(item -> ItemMapper.toDto(item))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Юзер c Id" + userId + " отсутствуют.");
        }
    }

    public ItemResponseDTO update(Long userId, Long itemId, ItemRequestDTO itemRequestDTO) {
        if (userRepository.isUserIdExists(userId) && itemRepository.isItemIdExists(itemId)) {
            Item item = ItemMapper.toModel(itemRequestDTO);
            item = itemRepository.update(item, itemId);
            return ItemMapper.toDto(item);
        } else {
            throw new NotFoundException("Юзер и/или вещь отсутствуют.");
        }
    }

    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}
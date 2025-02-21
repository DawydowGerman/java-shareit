package ru.practicum.shareit.item.service;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.expection.ValidationException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class ItemServiceImpl {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemDTO addNewItem(Long userId, ItemDTO itemDTO) {
        if (!userRepository.isUserIdExists(userId)) {
            log.error("Ошибка при обновлении данных юзера");
            throw new NotFoundException("Юзер отсутствуют");
        }
        validateDto(itemDTO);
        itemDTO.setOwnerId(userId);
        Item item = ItemMapper.toModel(itemDTO);
        item = itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    public List<ItemDTO> getItemsByUserid(Long userId) {
        if (userRepository.isUserIdExists(userId) &&
                itemRepository.getItemsByUserid(userId).isPresent()) {
            return itemRepository.getItemsByUserid(userId).get()
                    .stream()
                    .map(item -> ItemMapper.toDto(item))
                    .collect(Collectors.toList());
        }
        throw new NotFoundException("У юзера c Id " + userId + " отсутствуют вещи.");
    }

    public ItemDTO getItemById(Long userId, Long itemId) {
        if (userRepository.isUserIdExists(userId) && itemRepository.isItemIdExists(itemId)) {
            return ItemMapper.toDto(itemRepository.getItemById(itemId).get());
        } else {
            log.error("Ошибка при обновлении данных юзера");
            throw new NotFoundException("Юзер и/или вещь отсутствуют.");
        }
    }

    public List<ItemDTO> getItemsByText(String queryParam, Long userId) {
        if (userRepository.isUserIdExists(userId)) {
            return itemRepository.getItemsByText(queryParam).get()
                    .stream()
                    .map(item -> ItemMapper.toDto(item))
                    .collect(Collectors.toList());
        } else {
            log.error("Ошибка при обновлении данных юзера");
            throw new NotFoundException("Юзер c Id" + userId + " отсутствуют.");
        }
    }

    public ItemDTO update(Long userId, Long itemId, ItemDTO itemDTO) {
        if (userRepository.isUserIdExists(userId) && itemRepository.isItemIdExists(itemId)) {
            itemDTO.setId(itemId);
            Item item = ItemMapper.toModel(itemDTO);
            item = itemRepository.update(item);
            return ItemMapper.toDto(item);
        } else {
            log.error("Ошибка при обновлении данных юзера");
            throw new NotFoundException("Юзер и/или вещь отсутствуют.");
        }
    }

    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    private void validateDto(ItemDTO itemDTO) {
        if (itemDTO.getAvailable() == null) throw new ValidationException("У юзера отсутствует поле available");
        if (itemDTO.getName().isBlank()) throw new ValidationException("У юзера пустое поле name");
        if (itemDTO.getDescription() == null) throw new ValidationException("У юзера отсутствует поле description");
    }
}
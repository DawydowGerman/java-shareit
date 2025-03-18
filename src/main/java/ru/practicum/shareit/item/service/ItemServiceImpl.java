package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemJPARepository;
import ru.practicum.shareit.user.storage.UserJPARepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemJPARepository itemJPARepository;
    private final UserJPARepository userJPARepository;

    @Autowired
    public ItemServiceImpl(ItemJPARepository itemJPARepository, UserJPARepository userJPARepository) {
        this.itemJPARepository = itemJPARepository;
        this.userJPARepository = userJPARepository;
    }

    @Transactional
    public ItemResponseDTO addNewItem(Long userId, ItemRequestDTO itemRequestDTO) {
        if (!userJPARepository.existsById(userId)) {
            throw new NotFoundException("Юзер отсутствуют");
        }
        itemRequestDTO.setOwner(userJPARepository.findById(userId).get());
        Item item = ItemMapper.toModel(itemRequestDTO);
        item = itemJPARepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Transactional
    public List<ItemResponseDTO> getItemsByUserid(Long ownerId) {
        if (userJPARepository.existsById(ownerId)) {
            return itemJPARepository.findByOwnerId(ownerId)
                    .stream()
                    .map(item -> ItemMapper.toDto(item))
                    .collect(Collectors.toList());
        }
        throw new NotFoundException("У юзера c Id " + ownerId + " отсутствуют вещи.");
    }

    @Transactional
    public ItemResponseDTO getItemById(Long userId, Long itemId) {
        if (userJPARepository.existsById(userId) && itemJPARepository.existsById(itemId)) {
            return ItemMapper.toDto(itemJPARepository.findById(itemId).get());
        } else {
            throw new NotFoundException("Юзер и/или вещь отсутствуют.");
        }
    }

    @Transactional
    public List<ItemResponseDTO> getItemsByText(String queryParam, Long userId) {
        if (queryParam.isBlank()) {
            List<ItemResponseDTO> emtpytList = new ArrayList<>();
            return emtpytList;
        }
        if (userJPARepository.existsById(userId)) {
            return itemJPARepository.getItemsByText(queryParam)
                    .stream()
                    .filter(item -> item.getAvailable().equals(true))
                    .map(item -> ItemMapper.toDto(item))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Юзер c Id" + userId + " отсутствуют.");
        }
    }

    @Transactional
    public ItemResponseDTO update(Long userId, Long itemId, ItemRequestDTO itemRequestDTO) {
        if (userJPARepository.existsById(userId) && itemJPARepository.existsById(itemId)) {
            Item item = itemJPARepository.findById(itemId).get();
            for (String f : itemRequestDTO.getNonNullFields().get()) {
                if (f.equals("name")) item.setName(itemRequestDTO.getName());
                if (f.equals("description")) item.setDescription(itemRequestDTO.getDescription());
                if (f.equals("available")) item.setAvailable(itemRequestDTO.getAvailable());
            }
            item = itemJPARepository.save(item);
            return ItemMapper.toDto(item);
        } else {
            throw new NotFoundException("Юзер и/или вещь отсутствуют.");
        }
    }

    @Transactional
    public void deleteItem(long userId, long itemId) {
        itemJPARepository.deleteById(itemId);
    }
}
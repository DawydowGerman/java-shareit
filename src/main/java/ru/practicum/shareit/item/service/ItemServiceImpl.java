package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingJPARepository;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.expection.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentSpecification;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentJPARepository;
import ru.practicum.shareit.item.storage.ItemJPARepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserJPARepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class ItemServiceImpl implements ItemService {
    private final BookingJPARepository bookingJPARepository;
    private final ItemJPARepository itemJPARepository;
    private final UserJPARepository userJPARepository;
    private final CommentJPARepository commentRepository;

    @Autowired
    public ItemServiceImpl(BookingJPARepository bookingJPARepository, ItemJPARepository itemJPARepository,
                           UserJPARepository userJPARepository, CommentJPARepository commentRepository) {
        this.bookingJPARepository = bookingJPARepository;
        this.itemJPARepository = itemJPARepository;
        this.userJPARepository = userJPARepository;
        this.commentRepository = commentRepository;
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
    public CommentResponseDTO addNewComment(Long userId, Long itemId, CommentRequestDTO commentRequestDTO) {
        Item item = itemJPARepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " отсутствует."));
        User user = userJPARepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с ID " + userId + " отсутствует."));
        Booking booking = bookingJPARepository.findByBookerIdAndItemId(userId, itemId)
                .orElseThrow(() -> new NotFoundException("Бронирование отсутствует."));
        if (!booking.getBooker().getId().equals(userId) ||
                !booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Автором комментария должен быть арендатор либо не закончился период аренды.");
        }
        Comment comment = CommentMapper.toModel(commentRequestDTO);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }

    public List<ItemResponseDTO> getItemsByUserid(Long ownerId) {
        User user = userJPARepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Юзер с ID " + ownerId + " отсутствует."));
        List<Item> itemsList = itemJPARepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new NotFoundException("У юзера с ID " + ownerId + " отсутствуют вещи."));
        List<ItemResponseDTO> result = itemsList.stream()
                .map(item -> ItemMapper.toDto(item))
                .collect(Collectors.toList());
        Specification<Comment> commentSpec = null;
        for (ItemResponseDTO itemResp : result) {
            if (commentSpec == null) {
                commentSpec = Specification.where(CommentSpecification.hasItem(ItemMapper.toModelFromRespDTO(itemResp)));
            } else {
                commentSpec = commentSpec.and(CommentSpecification.hasItem(ItemMapper.toModelFromRespDTO(itemResp)));
            }
        }
        List<Comment> commentsList = commentRepository.findAll(commentSpec);
        result.forEach(ItemResp -> commentsList.forEach(comment -> {
            if (comment.getItem().getId().equals(ItemResp.getId())) ItemResp.addComment(comment);
                })
        );
        return result;
    }

    public ItemResponseDTO getItemById(Long userId, Long itemId) {
        Item item = itemJPARepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " отсутствует."));
        ItemResponseDTO itemResponseDTO = ItemMapper.toDto(item);
        User user = userJPARepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с ID " + userId + " отсутствует."));
        Optional<List<Comment>> commentsList = commentRepository.findByItemId(itemId);
        if (commentsList.get().size() > 0 && item.getOwner().getId().equals(userId)) {
            BookingResponseDTO lastBooking = BookingMapper.toDto(bookingJPARepository.findLastBookingByItemId(itemId));
            itemResponseDTO.setLastBooking(lastBooking);
            BookingResponseDTO nextBooking = BookingMapper.toDto(bookingJPARepository.findSecondLastBookingByItemId(itemId));
            itemResponseDTO.setNextBooking(nextBooking);
            itemResponseDTO.setComments(commentsList.get());
            return itemResponseDTO;
        } else if (commentsList.get().size() > 0) {
            itemResponseDTO.setLastBooking(null);
            itemResponseDTO.setNextBooking(null);
            itemResponseDTO.setComments(commentsList.get());
            return itemResponseDTO;
        } else return itemResponseDTO;
    }

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
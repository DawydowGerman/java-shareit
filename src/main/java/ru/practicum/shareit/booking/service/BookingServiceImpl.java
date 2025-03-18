package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.storage.BookingJPARepository;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.expection.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemJPARepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserJPARepository;

@Service
public class BookingServiceImpl {
    private final BookingJPARepository bookingJPARepository;
    private final ItemJPARepository itemJPARepository;
    private final UserJPARepository userJPARepository;

    @Autowired
    public BookingServiceImpl(BookingJPARepository bookingJPARepository,
                              ItemJPARepository itemJPARepository, UserJPARepository userJPARepository) {
        this.bookingJPARepository = bookingJPARepository;
        this.itemJPARepository = itemJPARepository;
        this.userJPARepository = userJPARepository;
    }

    @Transactional
    public BookingResponseDTO addNewBooking(Long bookerId, BookingRequestDTO bookingRequestDTO) {
        Item item = itemJPARepository.findById(bookingRequestDTO.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + bookingRequestDTO.getItemId() + " отсутствует."));
        User user = userJPARepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Юзер с ID " + bookerId + " отсутствует."));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с ID " + bookingRequestDTO.getItemId() + " не доступна.");
        }
        Booking booking = BookingMapper.toModel(bookingRequestDTO);
        booking.setItem(item);
        booking.setUser(user);
        booking.setStatus(Status.WAITING);
        booking = bookingJPARepository.save(booking);
        System.out.println("booking booker_id's: " + booking.getUser().getId());
        return BookingMapper.toDto(booking);
    }
}
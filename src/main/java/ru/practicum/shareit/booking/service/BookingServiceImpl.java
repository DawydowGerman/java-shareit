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
import ru.practicum.shareit.expection.ForbiddenException;
import ru.practicum.shareit.expection.InternalServerException;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.expection.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemJPARepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserJPARepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
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
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        booking = bookingJPARepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Transactional
    public BookingResponseDTO update(Long bookingId, Long ownerId, Boolean status) {
        Booking booking = bookingJPARepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " отсутствует."));
        User user = userJPARepository.findById(ownerId)
                .orElseThrow(() -> new ForbiddenException("Юзер с ID " + ownerId + " отсутствует."));
        if (!booking.getItem().getOwner().equals(user)) {
            throw new ForbiddenException("Юзер с id " + ownerId + " не является владельцем вещи.");
        }
        if (status.equals(true)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        booking = bookingJPARepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Transactional
    public BookingResponseDTO getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingJPARepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " отсутствует."));
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Юзер с ID " + userId + " не является владельцем вещи либо автором бронирования.");
        }
        return BookingMapper.toDto(booking);
    }

    @Transactional
    public List<BookingResponseDTO> getBookingByUserId(String state, Long userId) {
        User user = userJPARepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("Юзер с ID " + userId + " отсутствует."));
        switch (state) {
            case "CURRENT":
                return bookingJPARepository
                        .findByBookerIdAndCurrent(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            case "PAST":
                return bookingJPARepository
                        .findByBookerIdAndPast(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingJPARepository
                        .findByBookerIdAndFuture(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingJPARepository
                        .findByBookerIdAndStatus(userId, Status.WAITING)
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingJPARepository
                        .findByBookerIdAndStatus(userId, Status.REJECTED)
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            case "ALL":
                return bookingJPARepository.findByBookerId(userId)
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            default:
                return null;
        }
    }

    @Transactional
    public List<BookingResponseDTO> getBookingOfItemsByOwnerId(String state, Long userId) {
        User user = userJPARepository.findById(userId)
                .orElseThrow(() -> new InternalServerException("Юзер с ID " + userId + " отсутствует."));
        switch (state) {
            case "CURRENT":
                return bookingJPARepository
                        .findByItemOwnerIdAndCurrent(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            case "PAST":
                return bookingJPARepository
                        .findByItemOwnerIdAndPast(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingJPARepository
                        .findByItemOwnerIdAndFuture(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingJPARepository
                        .findByItemOwnerIdAndStatus(userId, Status.WAITING)
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingJPARepository
                        .findByItemOwnerIdAndStatus(userId, Status.REJECTED)
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            case "ALL":
                return bookingJPARepository.findByItemOwnerId(userId)
                        .stream()
                        .map(booking -> BookingMapper.toDto(booking))
                        .collect(Collectors.toList());
            default:
                return null;
        }
    }
}
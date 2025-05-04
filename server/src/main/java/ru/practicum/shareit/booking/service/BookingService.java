package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO addNewBooking(Long bookerId, BookingRequestDTO bookingRequestDTO);

    BookingResponseDTO update(Long bookingId, Long ownerId, Boolean status);

    BookingResponseDTO getBookingById(Long bookingId, Long userId);

    List<BookingResponseDTO> getBookingByUserId(String state, Long userId);

    List<BookingResponseDTO> getBookingOfItemsByOwnerId(String state, Long userId);
}
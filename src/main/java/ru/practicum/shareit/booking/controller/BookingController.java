package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingServiceImpl bookingServiceImpl;

    @PostMapping
    public BookingResponseDTO addNewItem(@PositiveOrZero @RequestHeader(SHARER_USER_ID) Long userId,
                                         @Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        log.info("Booking's addition: {}", bookingRequestDTO);
        return bookingServiceImpl.addNewBooking(userId, bookingRequestDTO);
    }


}

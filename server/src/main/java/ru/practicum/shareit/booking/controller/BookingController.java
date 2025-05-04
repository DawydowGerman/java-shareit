package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingServiceImpl;

    @PostMapping
    public BookingResponseDTO addNewItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                         @RequestBody BookingRequestDTO bookingRequestDTO) {
        log.info("Booking's addition: {}", bookingRequestDTO);
        return bookingServiceImpl.addNewBooking(userId, bookingRequestDTO);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDTO getBookingById(@RequestHeader(SHARER_USER_ID) Long userId,
                                             @PathVariable(name = "bookingId") Long bookingId) {
        log.info("Request of booking by ID: {}", bookingId);
        return bookingServiceImpl.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDTO> getBookingByUserId(@RequestHeader(SHARER_USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        log.info("Request of booking by user ID: {}", userId);
        return bookingServiceImpl.getBookingByUserId(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingResponseDTO> getBookingOfItemsByOwnerId(@RequestHeader(SHARER_USER_ID) Long userId,
                                                               @RequestParam(defaultValue = "ALL") String state) {
        log.info("Request of booking by user ID: {}", userId);
        return bookingServiceImpl.getBookingOfItemsByOwnerId(state, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDTO update(@RequestHeader(SHARER_USER_ID) Long userId,
                                     @PathVariable(name = "bookingId") Long bookingId,
                                     @RequestParam(required = false) Boolean approved) {
        log.info("Owner's approval: {}", approved);
        return bookingServiceImpl.update(bookingId, userId, approved);
    }
}
package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ContextConfiguration(classes = ShareItApp.class)
@WebMvcTest(controllers = BookingController.class)
@ActiveProfiles("test")
public class BookingControllerTestWithContext {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    ItemService itemService;

    BookingRequestDTO bookingRequest = new BookingRequestDTO(
            LocalDateTime.of(2025, 5, 1, 10, 0),
            LocalDateTime.of(2025, 5, 1, 12, 0),
            null
    );

    BookingResponseDTO bookingResponse = new BookingResponseDTO(
            1l,
            LocalDateTime.of(2023, 6, 15, 10, 0),
            LocalDateTime.of(2023, 6, 15, 12, 30),
            null,
            null,
            null
    );

    BookingResponseDTO bookingResponse1 = new BookingResponseDTO(
            2l,
            LocalDateTime.of(2023, 6, 15, 10, 0),
            LocalDateTime.of(2023, 6, 15, 12, 30),
            null,
            null,
            null
    );

    List<BookingResponseDTO> BookingResponseDTOList = Arrays.asList(bookingResponse, bookingResponse1);


    // 1. ========== addNewBooking Tests ==========
    @Test
    public void addNewBookingCreatedItem() throws Exception {
        when(bookingService.addNewBooking(anyLong(), any(BookingRequestDTO.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value("2023-06-15T10:00:00"))
                .andExpect(jsonPath("$.end").value("2023-06-15T12:30:00"));
    }

    @Test
    public void addNewBookingItemNotFoundException() throws Exception {
        when(bookingService.addNewBooking(anyLong(), any(BookingRequestDTO.class)))
                .thenThrow(new NotFoundException("Юзер отсутствует"));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addNewBookingWhenUserIdHeaderIsMissing() throws Exception {
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest());
    }

    // 2. ========== getBookingById Tests ==========
    @Test
    public void getBookingByIdOrdinaryCase() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(get("/bookings/{bookingId}", 1l)
                        .header("X-Sharer-User-Id", 2l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value("2023-06-15T10:00:00"))
                .andExpect(jsonPath("$.end").value("2023-06-15T12:30:00"));
    }

    @Test
    public void getBookingByIdNotFoundException() throws Exception {
        Long bookingId = 9L;

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Бронирование с ID " + bookingId + " отсутствует."));

        mvc.perform(get("/bookings/{bookingId}", 1l)
                        .header("X-Sharer-User-Id", 2l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getBookingByIdInvalidUserIdHeader() throws Exception {
        Long bookingId = 9L;

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBookingByIdWhenUserIdHeaderIsMissing() throws Exception {
        Long bookingId = 9L;

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // 3. ========== getBookingByUserId Tests ==========
    @Test
    public void getBookingByUserIdOrdinaryCase() throws Exception {
        when(bookingService.getBookingByUserId(anyString(), anyLong()))
                .thenReturn(BookingResponseDTOList);

        mvc.perform(get("/bookings", 1l)
                        .header("X-Sharer-User-Id", 2l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$[1].id").value(bookingResponse1.getId()))
                .andExpect(jsonPath("$[0].start").value("2023-06-15T10:00:00"))
                .andExpect(jsonPath("$[1].start").value("2023-06-15T10:00:00"));
    }

    @Test
    public void getBookingByUserIdNotFoundException() throws Exception {
        Long userId = 99l;

        when(bookingService.getBookingByUserId(anyString(), anyLong()))
                .thenThrow(new NotFoundException("Юзер с ID " + userId + " отсутствует."));

        mvc.perform(get("/bookings", 1l)
                        .header("X-Sharer-User-Id", 2l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getBookingByUserIdInvalidUserIdHeader() throws Exception {
        Long bookingId = 9L;

        mvc.perform(get("/bookings", bookingId)
                        .header("X-Sharer-User-Id", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBookingByUserIdWhenUserIdHeaderIsMissing() throws Exception {
        Long bookingId = 9L;

        mvc.perform(get("/bookings", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // 4. ========== getBookingOfItemsByOwnerId Tests ==========
    @Test
    public void getBookingOfItemsByOwnerIdOrdinaryCase() throws Exception {
        when(bookingService.getBookingOfItemsByOwnerId(anyString(), anyLong()))
                .thenReturn(BookingResponseDTOList);

        mvc.perform(get("/bookings/owner", 1l)
                        .header("X-Sharer-User-Id", 2l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$[1].id").value(bookingResponse1.getId()))
                .andExpect(jsonPath("$[0].start").value("2023-06-15T10:00:00"))
                .andExpect(jsonPath("$[1].start").value("2023-06-15T10:00:00"));
    }

    @Test
    public void getBookingOfItemsByOwnerIdNotFoundException() throws Exception {
        Long userId = 99l;

        when(bookingService.getBookingOfItemsByOwnerId(anyString(), anyLong()))
                .thenThrow(new NotFoundException("Юзер с ID " + userId + " отсутствует."));

        mvc.perform(get("/bookings/owner", 1l)
                        .header("X-Sharer-User-Id", 2l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getBookingOfItemsByOwnerIdInvalidUserIdHeader() throws Exception {
        Long bookingId = 9L;

        mvc.perform(get("/bookings/owner", bookingId)
                        .header("X-Sharer-User-Id", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBookingOfItemsByOwnerIdWhenUserIdHeaderIsMissing() throws Exception {
        Long bookingId = 9L;

        mvc.perform(get("/bookings/owner", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // 5. ========== update Tests ==========
    @Test
    public void updateOrdinaryCase() throws Exception {
        Long bookingId = 99l;
        Boolean approved = true;

        when(bookingService.update(anyLong(), anyLong(), any(Boolean.class)))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1l)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value("2023-06-15T10:00:00"))
                .andExpect(jsonPath("$.end").value("2023-06-15T12:30:00"));
    }
    @Test
    public void updateUserNotFoundException() throws Exception {
        Long ownerId = 99l;
        Long bookingId = 99l;

        when(bookingService.update(anyLong(), anyLong(), any(Boolean.class)))
                .thenThrow(new NotFoundException("Юзер с ID " + ownerId + " отсутствует."));

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1l)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateBookingNotFoundException() throws Exception {
        Long ownerId = 99l;
        Long bookingId = 99l;

        when(bookingService.update(anyLong(), anyLong(), any(Boolean.class)))
                .thenThrow(new NotFoundException("Бронирование с ID " + bookingId + " отсутствует."));

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1l)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
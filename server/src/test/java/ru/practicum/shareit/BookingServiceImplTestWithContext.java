package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.expection.ForbiddenException;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.expection.ValidationException;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ContextConfiguration(classes = ShareItApp.class)
@SpringBootTest
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookingServiceImplTestWithContext {
    @Autowired
    BookingServiceImpl bookingServiceImpl;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    private ItemServiceImpl itemService;

    // 1. ========== addNewBooking Tests ==========
    @Test
    public void addNewBookingCreateNewBooking() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO userBooker = userServiceImpl.saveUser(userRequestDTO1);

        BookingRequestDTO bookingRequest = new BookingRequestDTO(
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                item.getId()
        );
        BookingResponseDTO result = bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);

        assertNotNull(result.getId());
        assertEquals(result.getEnd(), bookingRequest.getEnd());
    }

    @Test
    public void addNewBookingNotExistsThrowNotFoundException() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO userBooker = userServiceImpl.saveUser(userRequestDTO1);

        Long nonExistentUserId = 99L;

        BookingRequestDTO bookingRequest = new BookingRequestDTO(
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                item.getId()
        );

        assertThrows(NotFoundException.class, () -> {
            bookingServiceImpl.addNewBooking(nonExistentUserId, bookingRequest);
        });
    }

    // 2. ========== update Tests ==========
    @Test
    public void updateOrdinaryCase() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO userBooker = userServiceImpl.saveUser(userRequestDTO1);

        BookingRequestDTO bookingRequest = new BookingRequestDTO(
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                item.getId()
        );
        BookingResponseDTO booking = bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);

        BookingResponseDTO result = bookingServiceImpl.update(booking.getId(), userOwner.getId(), true);

        assertEquals(result.getStatus(), Status.APPROVED);
    }

    @Test
    public void updateThrowForbiddenException() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO userBooker = userServiceImpl.saveUser(userRequestDTO1);

        BookingRequestDTO bookingRequest = new BookingRequestDTO(
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                item.getId()
        );
        BookingResponseDTO booking = bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);

        assertThrows(ForbiddenException.class, () -> {
            bookingServiceImpl.update(booking.getId(), userBooker.getId(), true);
        });
    }

    // 3. ========== getBookingById Tests ==========
    @Test
    public void getBookingByIdOrdinaryCase() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO userBooker = userServiceImpl.saveUser(userRequestDTO1);

        BookingRequestDTO bookingRequest = new BookingRequestDTO(
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                item.getId()
        );
        BookingResponseDTO booking = bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);
        BookingResponseDTO result = bookingServiceImpl.getBookingById(booking.getId(), userBooker.getId());

        assertEquals(result.getEnd(), booking.getEnd());
    }

    @Test
    public void getBookingByIdValidationException() {
        UserRequestDTO userRequestDTO3 = new UserRequestDTO("somemail3@gmail.com", "some3Name");
        UserResponseDTO someUser = userServiceImpl.saveUser(userRequestDTO3);

        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO userBooker = userServiceImpl.saveUser(userRequestDTO1);

        BookingRequestDTO bookingRequest = new BookingRequestDTO(
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                item.getId()
        );
        BookingResponseDTO booking = bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);

        BookingResponseDTO result = bookingServiceImpl.getBookingById(booking.getId(), userBooker.getId());

        assertThrows(ValidationException.class, () -> {
            bookingServiceImpl.getBookingById(booking.getId(), someUser.getId());
        });
    }

    // 4. ========== getBookingByUserId Tests ==========
    @Test
    public void getBookingByUserIdOrdinaryCase() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO userBooker = userServiceImpl.saveUser(userRequestDTO1);

        BookingRequestDTO bookingRequest = new BookingRequestDTO(
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                item.getId()
        );
        BookingResponseDTO booking = bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);

        List<BookingResponseDTO> result = bookingServiceImpl.getBookingByUserId("WAITING", userBooker.getId());

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    public void getBookingByUserIdForbiddenException() {
        Long nonexistentuser = 99L;

        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO userBooker = userServiceImpl.saveUser(userRequestDTO1);

        BookingRequestDTO bookingRequest = new BookingRequestDTO(
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                item.getId()
        );
        BookingResponseDTO booking = bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);

        assertThrows(ForbiddenException.class, () -> {
            bookingServiceImpl.getBookingByUserId("WAITING", nonexistentuser);
        });
    }

    // 5. ========== getBookingOfItemsByOwnerId Tests ==========
    @Test
    public void getBookingOfItemsByOwnerIdOrdinaryCase() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO userBooker = userServiceImpl.saveUser(userRequestDTO1);

        BookingRequestDTO bookingRequest = new BookingRequestDTO(
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                item.getId()
        );
        BookingResponseDTO booking = bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);

        List<BookingResponseDTO> result = bookingServiceImpl.getBookingOfItemsByOwnerId("WAITING", userOwner.getId());

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    public void getBookingOfItemsByOwnerIdEmptyResult() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO userBooker = userServiceImpl.saveUser(userRequestDTO1);

        BookingRequestDTO bookingRequest = new BookingRequestDTO(
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                item.getId()
        );
        BookingResponseDTO booking = bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);

        List<BookingResponseDTO> result = bookingServiceImpl.getBookingOfItemsByOwnerId("REJECTED", userOwner.getId());

        assertEquals(0, result.size());
    }


}

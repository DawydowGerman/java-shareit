package ru.practicum.shareit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.expection.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemJPARepository;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserJPARepository;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ContextConfiguration(classes = ShareItApp.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ItemServiceImplTestWithContext {
    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    BookingServiceImpl bookingServiceImpl;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    private UserJPARepository userJPARepository;

    @Autowired
    private ItemJPARepository itemJPARepository;

    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        testUser = new User(
                1L,
                "vlad@gmail.com",
                "Vladimir"
        );
        userJPARepository.save(testUser);
    }

    // 1. ========== addNewItem Tests ==========
    @Test
    public void addNewItemCreateNewItem() {
        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                testUser,
                null
        );

        ItemResponseDTO response = itemService.addNewItem(testUser.getId(), itemRequestDTO);

        assertNotNull(response.getId());
        assertEquals(itemRequestDTO.getDescription(), response.getDescription());
        assertTrue(itemJPARepository.findById(response.getId()).isPresent());
    }

    @Test
    @Transactional
    public void addNewItemUserNotExistsThrowNotFoundException() throws Exception {
        Long nonExistentUserId = 999L;

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO(
                "someName",
                "someDesc",
                true,
                testUser,
                null
        );

        assertThrows(NotFoundException.class, () -> {
            itemService.addNewItem(nonExistentUserId, itemRequestDTO);
        });
    }

    // 2. ========== addNewComment Tests ==========
    @Test
    @Transactional
    public void addNewCommentToItem() throws Exception {
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
        bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);

        CommentRequestDTO comment = new CommentRequestDTO("someComment");
        CommentResponseDTO commentResponse = itemService.addNewComment(userBooker.getId(), item.getId(), comment);

        assertNotNull(commentResponse.getId());
        assertEquals(commentResponse.getText(), comment.getText());
    }

    @Test
    @Transactional
    public void addNewCommentToItemWhenBookingPeriodNotExiredShouldThrowValidationException() throws Exception {
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
                LocalDateTime.of(2026, 5, 1, 12, 0),
                item.getId()
        );
        bookingServiceImpl.addNewBooking(userBooker.getId(), bookingRequest);

        CommentRequestDTO comment = new CommentRequestDTO("someComment");

        assertThrows(ValidationException.class, () -> {
            itemService.addNewComment(userBooker.getId(), item.getId(), comment);
        });
    }

    // 3. ========== getItemsByUserid Tests ==========
    @Test
    @Transactional
    public void getItemsByUseridOrdinaryCase() throws Exception {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO0 = new ItemRequestDTO(
                "someName0",
                "someDesc0",
                true,
                null,
                null
        );
        ItemResponseDTO item0 = itemService.addNewItem(userOwner.getId(), itemRequestDTO0);

        ItemRequestDTO itemRequestDTO1 = new ItemRequestDTO(
                "someName1",
                "someDesc1",
                true,
                null,
                null
        );
        ItemResponseDTO item1 = itemService.addNewItem(userOwner.getId(), itemRequestDTO1);

        List<ItemResponseDTO> result = itemService.getItemsByUserid(userOwner.getId());

        assertEquals(2, result.size());
        assertEquals(item0.getId(), result.get(0).getId());
    }

    @Test
    @Transactional
    public void getItemsByUserHasNoItemsShouldThrowNotFoundException() throws Exception {
        Long nonExistentUserId = 999L;
                assertThrows(NotFoundException.class, () -> {
            itemService.getItemsByUserid(nonExistentUserId);
        });
    }

    // 4. ========== getItemById Tests ==========
    @Test
    @Transactional
    public void getItemByIdOrdinaryCase() throws Exception {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO0 = new ItemRequestDTO(
                "someName3",
                "someDesc3",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(userOwner.getId(), itemRequestDTO0);
        ItemResponseDTO result = itemService.getItemById(userOwner.getId(), item.getId());

        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    @Transactional
    public void getItemByIdTriesToRetrieveNonExistentItems() throws Exception {
        Long nonExistentitem = 999L;

        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO userOwner = userServiceImpl.saveUser(userRequestDTO0);

        assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(userOwner.getId(),nonExistentitem);
        });
    }

    // 5. ========== getItemsByText Tests ==========
    @Test
    @Transactional
    public void getItemsByTextOrdinaryCase() throws Exception {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail5@gmail.com", "some5Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO0 = new ItemRequestDTO(
                "someName4",
                "someDesc4",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(user.getId(), itemRequestDTO0);
        List<ItemResponseDTO> result = itemService.getItemsByText("someName4", user.getId());

        assertEquals(1, result.size());
        assertEquals(item.getName(), result.get(0).getName());
    }

    @Test
    @Transactional
    public void getItemsByTextTriesToRetrieveNonExistentItems() throws Exception {
        Long nonExistentUser = 999L;

        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail5@gmail.com", "some5Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO0 = new ItemRequestDTO(
                "someName4",
                "someDesc4",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(user.getId(), itemRequestDTO0);

        assertThrows(NotFoundException.class, () -> {
            itemService.getItemsByText("someName4", nonExistentUser);
        });
    }

    // 6. ========== update Tests ==========
    @Test
    @Transactional
    public void updateOrdinaryCase() throws Exception {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail6@gmail.com", "some6Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO0 = new ItemRequestDTO(
                "someName6",
                "someDesc6",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(user.getId(), itemRequestDTO0);

        ItemRequestDTO updateRequest = new ItemRequestDTO(
                "someName6",
                "updatedDesc",
                true,
                null,
                null
        );

        ItemResponseDTO result = itemService.update(user.getId(), item.getId(), updateRequest);
        assertEquals(updateRequest.getDescription(), result.getDescription());
    }

    @Test
    @Transactional
    public void updateTriesToUpdateNonExistentItems() throws Exception {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail6@gmail.com", "some6Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        Long nonExistentItem = 999L;

        ItemRequestDTO updateRequest = new ItemRequestDTO(
                "someName6",
                "updatedDesc",
                true,
                null,
                null
        );

        assertThrows(NotFoundException.class, () -> {
            itemService.update(user.getId(), nonExistentItem, updateRequest);
        });
    }

    // 6. ========== delete Tests ==========
    @Test
    @Transactional
    public void deleteItemOrdinaryCase() throws Exception {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail7@gmail.com", "some6Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        ItemRequestDTO itemRequestDTO0 = new ItemRequestDTO(
                "someName7",
                "someDesc7",
                true,
                null,
                null
        );
        ItemResponseDTO item = itemService.addNewItem(user.getId(), itemRequestDTO0);

        assertDoesNotThrow(() ->
                itemService.deleteItem(user.getId(), item.getId())
        );
    }

    @Test
    @Transactional
    public void deleteNonExistentItemShouldThrowNotFoundException() throws Exception {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail8@gmail.com", "some8Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        Long nonExistentItem = 999L;

        assertThrows(NotFoundException.class, () -> {
            itemService.deleteItem(user.getId(), nonExistentItem);
        });
    }
}

package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserJPARepository;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ContextConfiguration(classes = ShareItApp.class)
@SpringBootTest
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserServiceImplTestWithContext {
    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    private UserJPARepository userJPARepository;

    // 1. ========== saveUser Test ==========
    @Test
    public void saveUserCreateNewUser() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        assertNotNull(user.getId());
        assertEquals(userRequestDTO0.getEmail(), user.getEmail());
        assertTrue(userJPARepository.findById(user.getId()).isPresent());
    }

    // 2. ========== getAllUsers Test ==========
    @Test
    public void getAllUsersOrdinaryCase() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO user0 = userServiceImpl.saveUser(userRequestDTO0);

        UserRequestDTO userRequestDTO1 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO user1 = userServiceImpl.saveUser(userRequestDTO1);

        List<UserResponseDTO> result = userServiceImpl.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(user0.getEmail(), result.get(0).getEmail());
    }

    @Test
    public void getAllUsersReturnsNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            userServiceImpl.getAllUsers();
        });
    }

    // 3. ========== getUserById Test ==========
    @Test
    public void getUserByIdOrdinaryCase() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail1@gmail.com", "some1Name");
        UserResponseDTO user0 = userServiceImpl.saveUser(userRequestDTO0);

        UserResponseDTO result = userServiceImpl.getUserById(user0.getId());
        assertEquals(user0.getEmail(), result.getEmail());
    }

    @Test
    public void  getUserByIdReturnsNotFoundException() {
        Long nonExistentuserID = 999L;

        assertThrows(NotFoundException.class, () -> {
            userServiceImpl.getUserById(nonExistentuserID);
        });
    }

    // 4. ========== update Test ==========
    @Test
    @Transactional
    public void updateOrdinaryCase() throws Exception {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail4@gmail.com", "some4Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        UserRequestDTO updatedUserRequest = new UserRequestDTO("updated@gmail.com", "some1Name");
        UserResponseDTO result = userServiceImpl.update(user.getId(), updatedUserRequest);

        assertEquals(result.getEmail(), updatedUserRequest.getEmail());
    }

    @Test
    public void  updateReturnsNotFoundException() {
        Long nonExistentuserID = 999L;
        UserRequestDTO updatedUserRequest = new UserRequestDTO("updated@gmail.com", "some1Name");

        assertThrows(NotFoundException.class, () -> {
            userServiceImpl.update(nonExistentuserID, updatedUserRequest);
        });
    }

    // 5. ========== remove Test ==========
    @Test
    @Transactional
    public void removeOrdinaryCase() throws Exception {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail4@gmail.com", "some4Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        assertDoesNotThrow(() ->
                userServiceImpl.remove(user.getId())
        );
    }

    @Test
    @Transactional
    public void removeNonExistentItemShouldThrowNotFoundException() throws Exception {
        Long nonExistentItem = 999L;

        assertThrows(NotFoundException.class, () -> {
            userServiceImpl.remove(nonExistentItem);
        });
    }

}

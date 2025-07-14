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
import ru.practicum.shareit.request.dto.RequestIncomingDTO;
import ru.practicum.shareit.request.dto.RequestOutcomingDTO;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserJPARepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@ContextConfiguration(classes = ShareItApp.class)
@SpringBootTest
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RequestServiceImplTestWithContext {
    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    RequestServiceImpl requestServiceImpl;

    @Autowired
    private UserJPARepository userJPARepository;

    // 1. ========== addNewRequest Test ==========
    @Test
    public void addNewRequestCreateNewReq() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        RequestIncomingDTO request = new RequestIncomingDTO("SomeDesc", LocalDateTime.now(), null);

        RequestOutcomingDTO result = requestServiceImpl.addNewRequest(user.getId(), request);

        assertNotNull(user.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertTrue(userJPARepository.findById(user.getId()).isPresent());
    }

    @Test
    public void addNewRequestNonExistentUser() {
        Long nonExistentuser = 999L;

        RequestIncomingDTO request = new RequestIncomingDTO("SomeDesc", LocalDateTime.now(), null);

        assertThrows(NotFoundException.class, () -> {
            requestServiceImpl.addNewRequest(nonExistentuser, request);
        });
    }

    // 2. ========== getAllRequests Test ==========
    @Test
    public void getAllRequestsOrdinaryCase() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        RequestIncomingDTO request = new RequestIncomingDTO("SomeDesc", LocalDateTime.now(), null);
        requestServiceImpl.addNewRequest(user.getId(), request);

        RequestIncomingDTO request0 = new RequestIncomingDTO("SomeDesc0", LocalDateTime.now(), null);
        requestServiceImpl.addNewRequest(user.getId(), request0);

        List<RequestOutcomingDTO> result = requestServiceImpl.getAllRequests(user.getId());

        assertEquals(2, result.size());
    }

    @Test
    public void getAllRequestsReturnsEmptyList() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        List<RequestOutcomingDTO> result = requestServiceImpl.getAllRequests(user.getId());

        assertEquals(0, result.size());
    }

    // 3. ========== getOwnRequests Test ==========
    @Test
    public void getOwnRequestsOrdinaryCase() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        RequestIncomingDTO request = new RequestIncomingDTO("SomeDesc", LocalDateTime.now(), null);
        requestServiceImpl.addNewRequest(user.getId(), request);

        RequestIncomingDTO request0 = new RequestIncomingDTO("SomeDesc0", LocalDateTime.now(), null);
        requestServiceImpl.addNewRequest(user.getId(), request0);

        List<RequestOutcomingDTO> result = requestServiceImpl.getOwnRequests(user.getId());

        assertEquals(2, result.size());
        assertEquals(request.getDescription(), result.get(1).getDescription());
    }

    @Test
    public void getOwnRequestsEmpty() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        List<RequestOutcomingDTO> result = requestServiceImpl.getOwnRequests(user.getId());

        assertEquals(0, result.size());
    }

    // 4. ========== getRequestById Test ==========
    @Test
    public void getRequestByIdOrdinaryCase() {
        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        RequestIncomingDTO requestIncom = new RequestIncomingDTO("SomeDesc", LocalDateTime.now(), null);
        RequestOutcomingDTO requestOut = requestServiceImpl.addNewRequest(user.getId(), requestIncom);

        RequestOutcomingDTO result = requestServiceImpl.getRequestById(user.getId(), requestOut.getId());

        assertEquals(requestIncom.getDescription(), result.getDescription());
    }

    @Test
    public void getRequestByIdNonExistentuser() {
        Long nonExistentuser = 999L;

        UserRequestDTO userRequestDTO0 = new UserRequestDTO("somemail0@gmail.com", "some0Name");
        UserResponseDTO user = userServiceImpl.saveUser(userRequestDTO0);

        RequestIncomingDTO requestIncom = new RequestIncomingDTO("SomeDesc", LocalDateTime.now(), null);
        RequestOutcomingDTO requestOut = requestServiceImpl.addNewRequest(user.getId(), requestIncom);

        assertThrows(NotFoundException.class, () -> {
            requestServiceImpl.getRequestById(nonExistentuser, requestOut.getId());
        });
    }
}
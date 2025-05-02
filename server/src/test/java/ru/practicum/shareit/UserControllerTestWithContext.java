package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.service.UserService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ContextConfiguration(classes = ShareItApp.class)
@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
public class UserControllerTestWithContext {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    UserRequestDTO userRequest = new UserRequestDTO(
            "somemail@gmail.com",
            "someName"
    );
    UserResponseDTO userResponce = new UserResponseDTO(
            1l,
            "somemail@gmail.com",
            "someName"
    );
    UserResponseDTO userResponce0 = new UserResponseDTO(
            2l,
            "somemail0@gmail.com",
            "someName0"
    );
    List<UserResponseDTO> resultList = Arrays.asList(userResponce, userResponce0);

    // 1. ========== saveUser Tests ==========
    @Test
    public void saveUserCreatedUser() throws Exception {
        when(userService.saveUser(any(UserRequestDTO.class)))
                .thenReturn(userResponce);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponce.getId()))
                .andExpect(jsonPath("$.email").value(userResponce.getEmail()))
                .andExpect(jsonPath("$.name").value(userResponce.getName()));

        verify(userService, times(1)).saveUser(any(UserRequestDTO.class));
    }

    @Test
    void addNewUserNullBodyBadRequest() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewUserWhenMalformedJsonBadRequest() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed}"))
                .andExpect(status().isBadRequest());
    }

    // 2. ========== getAllUsers Tests ==========
    @Test
    void getItemsTestReturnsList() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(resultList);

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userResponce.getId()))
                .andExpect(jsonPath("$[1].id").value(userResponce0.getId()))
                .andExpect(jsonPath("$[0].email").value(userResponce.getEmail()))
                .andExpect(jsonPath("$[1].email").value(userResponce0.getEmail()))
                .andExpect(jsonPath("$[0].name").value(userResponce.getName()))
                .andExpect(jsonPath("$[1].name").value(userResponce0.getName()));
    }

    @Test
    void getItemsNoItemsFoundNotFoundException() throws Exception {
        when(userService.getAllUsers())
                .thenThrow(new NotFoundException("Список юзеров пуст."));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // 3. ========== getUserById Tests ==========
    @Test
    public void getUserByIdOrdinaryCase() throws Exception {
        Long userId = 9L;

        when(userService.getUserById(anyLong()))
                .thenReturn(userResponce);

        mvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponce.getId()))
                .andExpect(jsonPath("$.email").value(userResponce.getEmail()))
                .andExpect(jsonPath("$.name").value(userResponce.getName()));
    }

    @Test
    public void getUserByIdNotFoundException() throws Exception {
        Long userId = 9L;

        when(userService.getUserById(anyLong()))
                .thenThrow(new NotFoundException("Юзер c Id " + userId + " отсутствует."));

        mvc.perform(get("/items/{itemId}", userId)
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // 4. ========== update Tests ==========
    @Test
    public void updateOrdinaryCase() throws Exception {
        when(userService.update(anyLong(), any(UserRequestDTO.class)))
                .thenReturn(userResponce);

        mvc.perform(patch("/users/{userId}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponce.getId()))
                .andExpect(jsonPath("$.email").value(userResponce.getEmail()))
                .andExpect(jsonPath("$.name").value(userResponce.getName()));
    }

    @Test
    public void updateNotFoundException() throws Exception {
        when(userService.update(anyLong(), any(UserRequestDTO.class)))
                .thenThrow(new NotFoundException("Юзер отсутствуют"));

        mvc.perform(patch("/users/{userId}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateWhenNullBodyThenBadRequest() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateWhenMalformedJsonThenBadRequest() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed}"))
                .andExpect(status().isBadRequest());
    }

    // 5. ========== remove Tests ==========
    @Test
    void deleteItemOrdinaryCase() throws Exception {
        mvc.perform(delete("/users/{userId}", 19))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItemNoSuchUserExists() throws Exception {
        Long id = 99l;
        doThrow(new NotFoundException("Юзер не найден с id = " + id))
                .when(userService)
                .remove(id);
        mvc.perform(delete("/users/{userId}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
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
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestIncomingDTO;
import ru.practicum.shareit.request.dto.RequestOutcomingDTO;
import ru.practicum.shareit.request.model.AnswerToRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ContextConfiguration(classes = ShareItApp.class)
@WebMvcTest(controllers = RequestController.class)
@ActiveProfiles("test")
public class RequestControllerTestWithContext {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    private MockMvc mvc;

    User user = new User(
            1l,
            "some@mail.ru",
            "someName"
    );

    AnswerToRequest answerToRequest0 = new AnswerToRequest(
            1l,
            "someName0",
            "someDesc0",
            2l
    );

    AnswerToRequest answerToRequest1 = new AnswerToRequest(
            2l,
            "someName1",
            "someDesc1",
            4l
    );

    List<AnswerToRequest> answersList = Arrays.asList(answerToRequest0, answerToRequest1);

    RequestIncomingDTO incomingDTO0 = new RequestIncomingDTO(
            "someDesc0",
            LocalDateTime.now(),
            null
    );

    RequestIncomingDTO incomingDTO1 = new RequestIncomingDTO(
            "someDesc1",
            LocalDateTime.now(),
            null
    );

    RequestOutcomingDTO outcomingDTO = new RequestOutcomingDTO(
            1l,
            "someDesc",
            LocalDateTime.now(),
            user,
            null
    );

    RequestOutcomingDTO outcomingDTO1 = new RequestOutcomingDTO(
            2l,
            "someDesc1",
            LocalDateTime.now(),
            user,
            null
    );

    List<RequestOutcomingDTO> outcomingList = Arrays.asList(outcomingDTO, outcomingDTO1);

    // 1. ========== addNewRequest Tests ==========
    @Test
    public void addNewRequestCreatedItem() throws Exception {
        when(requestService.addNewRequest(anyLong(), any(RequestIncomingDTO.class)))
                .thenReturn(outcomingDTO);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incomingDTO0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(outcomingDTO.getId()))
                .andExpect(jsonPath("$.description").value(outcomingDTO.getDescription()));
    }

    @Test
    public void addNewRequestNotFoundException() throws Exception {
        when(requestService.addNewRequest(anyLong(), any(RequestIncomingDTO.class)))
                .thenThrow(new NotFoundException("Юзер отсутствуют"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incomingDTO0)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addNewRequestWhenUserIdHeaderIsMissing() throws Exception {
        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incomingDTO0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNewRequestInvalidUserIdHeader() throws Exception {
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incomingDTO0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNewRequestNullBody() throws Exception {
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNewRequestMalformedJson() throws Exception {
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed}"))
                .andExpect(status().isBadRequest());
    }

    // 2. ========== getOwnRequests Tests ==========
    @Test
    public void getOwnRequestOrdinaryCase() throws Exception {
        when(requestService.getOwnRequests(anyLong()))
                .thenReturn(outcomingList);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(outcomingDTO.getId()))
                .andExpect(jsonPath("$[1].id").value(outcomingDTO1.getId()))
                .andExpect(jsonPath("$[0].description").value(outcomingDTO.getDescription()))
                .andExpect(jsonPath("$[1].description").value(outcomingDTO1.getDescription()));
    }

    @Test
    public void getOwnRequestNotFoundException() throws Exception {
        Long userId = 9L;

        when(requestService.getOwnRequests(anyLong()))
                .thenThrow(new NotFoundException("Юзер с ID " + userId + " отсутствует."));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // 3. ========== getAllRequests Tests ==========
    @Test
    public void getAllRequestsOrdinaryCase() throws Exception {
        when(requestService.getAllRequests(anyLong()))
                .thenReturn(outcomingList);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(outcomingDTO.getId()))
                .andExpect(jsonPath("$[1].id").value(outcomingDTO1.getId()))
                .andExpect(jsonPath("$[0].description").value(outcomingDTO.getDescription()))
                .andExpect(jsonPath("$[1].description").value(outcomingDTO1.getDescription()));
    }

    @Test
    public void getAllRequestsNotFoundException() throws Exception {
        Long userId = 9L;

        when(requestService.getAllRequests(anyLong()))
                .thenThrow(new NotFoundException("Юзер с ID " + userId + " отсутствует."));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemsByTextEmptyList() throws Exception {
        when(requestService.getAllRequests(anyLong()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // 4. ========== getRequestById Tests ==========
    @Test
    public void getRequestByIdOrdinaryCase() throws Exception {
        Long requestId = 9L;

        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(outcomingDTO);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(outcomingDTO.getId()))
                .andExpect(jsonPath("$.description").value(outcomingDTO.getDescription()));
    }

    @Test
    public void getRequestByIdNotFoundException() throws Exception {
        Long itemId = 9L;

        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Вещь с ID " + itemId + " отсутствует."));

        mvc.perform(get("/requests/{requestId}", itemId)
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemByIdInvalidUserIdHeader() throws Exception {
        Long itemId = 9L;

        mvc.perform(get("/requests/{requestId}", itemId)
                        .header("X-Sharer-User-Id", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

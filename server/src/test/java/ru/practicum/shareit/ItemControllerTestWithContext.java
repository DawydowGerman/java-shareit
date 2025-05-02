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
import ru.practicum.shareit.expection.ValidationException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.service.ItemService;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ContextConfiguration(classes = ShareItApp.class)
@WebMvcTest(controllers = ItemController.class)
@ActiveProfiles("test")
public class ItemControllerTestWithContext {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    ItemRequestDTO itemReq = new ItemRequestDTO(
            "SomeName",
            "SomeDesc",
            true,
            null,
            null
    );

    ItemResponseDTO itemResp = new ItemResponseDTO(
            1L,
            "SomeName",
            "SomeDesc",
            true,
            null,
            null,
            null,
            null,
            null
    );

    CommentRequestDTO commentRequest = new CommentRequestDTO(
            "someComment"
    );

    CommentResponseDTO commentResponse = new CommentResponseDTO(
            1l,
            "someComment",
            "someName",
            LocalDateTime.now()
    );

    // 1. ========== addNewItem Tests ==========
    @Test
    public void addNewItemCreatedItem() throws Exception {
        when(itemService.addNewItem(anyLong(), any(ItemRequestDTO.class)))
                .thenReturn(itemResp);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResp.getId()))
                .andExpect(jsonPath("$.name").value(itemResp.getName()))
                .andExpect(jsonPath("$.description").value(itemResp.getDescription()));
    }

    @Test
    public void addNewItemNotFoundException() throws Exception {
        when(itemService.addNewItem(anyLong(), any(ItemRequestDTO.class)))
                .thenThrow(new NotFoundException("Юзер отсутствуют"));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addNewItemWhenUserIdHeaderIsMissing() throws Exception {
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNewItemInvalidUserIdHeader() throws Exception {
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq))) //
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNewItemNullBody() throws Exception {
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNewItemMalformedJson() throws Exception {
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed}"))
                .andExpect(status().isBadRequest());
    }

    // 2. ========== addNewComment Tests ==========
    @Test
    public void addNewCommentCreatedComment() throws Exception {
        Long itemId = 9L;

        when(itemService.addNewComment(anyLong(), anyLong(), any(CommentRequestDTO.class)))
                .thenReturn(commentResponse);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponse.getId()))
                .andExpect(jsonPath("$.text").value(commentResponse.getText()))
                .andExpect(jsonPath("$.authorName").value(commentResponse.getAuthorName()));
    }

    @Test
    public void addNewCommentNotFoundException() throws Exception {
        Long itemId = 9L;
        Long userId = 10l;

        when(itemService.addNewComment(anyLong(), anyLong(), any(CommentRequestDTO.class)))
                .thenThrow(new NotFoundException("Юзер с ID " + userId + " отсутствует."));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addNewCommentValidationException() throws Exception {
        Long itemId = 9L;
        Long userId = 10l;

        when(itemService.addNewComment(anyLong(), anyLong(), any(CommentRequestDTO.class)))
                .thenThrow(new ValidationException("Автором комментария должен быть арендатор либо не закончился период аренды."));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNewCommentHeaderIsMissing() throws Exception {
        Long itemId = 9L;

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNewCommentInvalidUserIdHeader() throws Exception {
        Long itemId = 9L;

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq))) //
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNewCommentNullBody() throws Exception {
        Long itemId = 9L;

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNewCommentMalformedJson() throws Exception {
        Long itemId = 9L;

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed}"))
                .andExpect(status().isBadRequest());
    }

    // 3. ========== getItemsByUserid Tests ==========
    @Test
    public void getItemsByUseridOrdinaryCase() throws Exception {
        Long userId = 9L;

        ItemResponseDTO itemResp0 = new ItemResponseDTO(
                1L,
                "SomeName",
                "SomeDesc",
                true,
                null,
                null,
                null,
                null,
                null
        );
        ItemResponseDTO itemResp1 = new ItemResponseDTO(
                2L,
                "Some0Name",
                "Some0Desc",
                true,
                null,
                null,
                null,
                null,
                null
        );
        List<ItemResponseDTO> list = Arrays.asList(itemResp0, itemResp1);

        when(itemService.getItemsByUserid(anyLong()))
                .thenReturn(list);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResp0.getId()))
                .andExpect(jsonPath("$[1].id").value(itemResp1.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResp0.getName()))
                .andExpect(jsonPath("$[1].name").value(itemResp1.getName()))
                .andExpect(jsonPath("$[0].description").value(itemResp0.getDescription()))
                .andExpect(jsonPath("$[1].description").value(itemResp1.getDescription()));
    }

    @Test
    public void getItemsByUseridNotFoundException() throws Exception {
        Long userId = 9L;

        when(itemService.getItemsByUserid(anyLong()))
                .thenThrow(new NotFoundException("Юзер с ID " + userId + " отсутствует."));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq)))
                .andExpect(status().isNotFound());
    }

    // 4. ========== getItemById Tests ==========
    @Test
    public void getItemByIdOrdinaryCase() throws Exception {
        Long itemId = 9L;

        ItemResponseDTO itemResp0 = new ItemResponseDTO(
                1L,
                "SomeName",
                "SomeDesc",
                true,
                null,
                null,
                null,
                null,
                null
        );

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemResp0);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResp0.getId()))
                .andExpect(jsonPath("$.name").value(itemResp0.getName()));
    }

    @Test
    public void getItemByIdNotFoundException() throws Exception {
        Long itemId = 9L;

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Вещь с ID " + itemId + " отсутствует."));

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemByIdInvalidUserIdHeader() throws Exception {
        Long itemId = 9L;

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getItemByIdWhenUserIdHeaderIsMissing() throws Exception {
        Long itemId = 9L;

        mvc.perform(get("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq)))
                .andExpect(status().isBadRequest());
    }

    // 5. ========== getItemsByText Tests ==========
    @Test
    public void getItemsByTextOrdinaryCase() throws Exception {
        ItemResponseDTO itemResp0 = new ItemResponseDTO(
                1L,
                "SomeName",
                "SomeDesc",
                true,
                null,
                null,
                null,
                null,
                null
        );
        ItemResponseDTO itemResp1 = new ItemResponseDTO(
                2L,
                "Some0Name",
                "SomeDesc",
                true,
                null,
                null,
                null,
                null,
                null
        );
        List<ItemResponseDTO> list = Arrays.asList(itemResp0, itemResp1);

        when(itemService.getItemsByText(anyString(), anyLong()))
                .thenReturn(list);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "18")
                        .param("text", "SomeDesc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResp0.getId()))
                .andExpect(jsonPath("$[1].id").value(itemResp1.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResp0.getName()))
                .andExpect(jsonPath("$[1].name").value(itemResp1.getName()))
                .andExpect(jsonPath("$[0].description").value(itemResp0.getDescription()))
                .andExpect(jsonPath("$[1].description").value(itemResp1.getDescription()));
    }

    @Test
    public void getItemsByTextEmptyList() throws Exception {
        when(itemService.getItemsByText(anyString(), anyLong()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getItemsByTextNotFoundException() throws Exception {
        when(itemService.getItemsByText(anyString(), anyLong()))
                .thenThrow(new NotFoundException("Юзер отсутствуют"));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "18")
                        .param("text", "SomeDesc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemsByTextInvalidUserIdHeader() throws Exception {
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "not-a-number")
                        .param("text", "SomeDesc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // 6. ========== update Tests ==========
    @Test
    public void updateOrdinaryCase() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemRequestDTO.class)))
                .thenReturn(itemResp);

        mvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResp.getId()))
                .andExpect(jsonPath("$.name").value(itemResp.getName()))
                .andExpect(jsonPath("$.description").value(itemResp.getDescription()));
    }

    @Test
    public void updateNotFoundException() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemRequestDTO.class)))
                .thenThrow(new NotFoundException("Юзер и/или вещь отсутствуют."));

        mvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateHeaderIsMissing() throws Exception {
        mvc.perform(patch("/items/{itemId}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateInvalidUserIdHeader() throws Exception {
        mvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemReq))) //
                .andExpect(status().isBadRequest());
    }

    // 7. ========== delete Tests ==========
    @Test
    void deleteItemOrdinaryCase() throws Exception {
        mvc.perform(delete("/items/{itemId}", 19)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItemNoSuchItemExists() throws Exception {
        Long userId = 1L;
        Long itemId = 999L;

        doThrow(new NotFoundException("Вещь с ID " + itemId + " отсутствует."))
                .when(itemService)
                .deleteItem(userId, itemId);

        mvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Вещь с ID " + itemId + " отсутствует."));
    }

    @Test
    void deleteItemNoSuchUserExists() throws Exception {
        Long userId = 23L;
        Long itemId = 19L;

        doThrow(new NotFoundException("Юзер с ID " + userId + " отсутствует."))
                .when(itemService)
                .deleteItem(userId, itemId);

        mvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Юзер с ID " + userId + " отсутствует."));
    }

    @Test
    public void deleteWhenUserIdHeaderIsMissing() throws Exception {
        Long itemId = 999L;

        mvc.perform(delete("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteWhenInvalidUserIdHeader() throws Exception {
        Long itemId = 999L;
        mvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
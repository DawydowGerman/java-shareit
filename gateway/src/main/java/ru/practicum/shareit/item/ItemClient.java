package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addNewItem(long userId, ItemRequestDTO itemRequestDTO) {
        return post("", userId, itemRequestDTO);
    }

    public ResponseEntity<Object> addNewComment(long userId, Long itemId, CommentRequestDTO commentRequestDTO) {
        return post("/" + itemId + "/comment", userId, commentRequestDTO);
    }

    public ResponseEntity<Object> getItemsByUserid(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemById(long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByText(String textValue, Long itemId) {
        return get("/search?text=" + textValue, itemId);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemRequestDTO itemRequestDTO) {
        return patch("/" + itemId, userId, itemRequestDTO);
    }

    public ResponseEntity<Object> delete(Long userId, Long itemId) {
        return delete("/" + itemId, userId);
    }
}
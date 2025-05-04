package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.RequestIncomingDTO;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addNewRequest(long userId, RequestIncomingDTO incomingDTO) {
        return post("", userId, incomingDTO);
    }

    public ResponseEntity<Object> getOwnRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getRequestById(long userId, Long requestId) {
        return get("/" + requestId, userId);
    }


}

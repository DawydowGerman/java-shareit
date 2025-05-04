package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestIncomingDTO;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody RequestIncomingDTO incomingDTO) {
        log.info("Item's addition: {}", incomingDTO);
        return requestClient.addNewRequest(userId, incomingDTO);
    }

    @GetMapping()
    public ResponseEntity<Object> getOwnRequests(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Request things by userId: {}", userId);
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Request all things by userId: {}", userId);
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable(name = "requestId") Long requestId) {
        log.info("Request request by id: {}", requestId);
        return requestClient.getRequestById(userId, requestId);
    }


}

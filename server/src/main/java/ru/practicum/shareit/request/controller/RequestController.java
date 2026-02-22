package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestIncomingDTO;
import ru.practicum.shareit.request.dto.RequestOutcomingDTO;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    public RequestOutcomingDTO addNewRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                             @RequestBody RequestIncomingDTO incomingDTO) {
        log.info("Item's addition: {}", incomingDTO);
        return requestService.addNewRequest(userId, incomingDTO);
    }

    @GetMapping()
    public List<RequestOutcomingDTO> getOwnRequests(
                         @RequestHeader(value = SHARER_USER_ID, required = false) Long userId) {
        log.info("Request things by userId: {}", userId);
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestOutcomingDTO> getAllRequests(
            @RequestHeader(value = SHARER_USER_ID, required = false) Long userId) {
        log.info("Request all things by userId: {}", userId);
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public RequestOutcomingDTO getRequestById(
          @RequestHeader(value = SHARER_USER_ID, required = false) Long userId,
          @PathVariable(name = "requestId") Long requestId) {
        log.info("Request request by id: {}", requestId);
        return requestService.getRequestById(userId, requestId);
    }

    @PatchMapping("/{requestId}")
    public RequestOutcomingDTO update(@PathVariable(name = "requestId") Long id,
                                      @RequestBody RequestIncomingDTO requestDTO) {
        log.info("Request's update: {}", requestDTO);
        return requestService.update(id, requestDTO);
    }
}
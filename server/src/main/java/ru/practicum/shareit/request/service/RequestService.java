package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestIncomingDTO;
import ru.practicum.shareit.request.dto.RequestOutcomingDTO;

import java.util.List;

public interface RequestService {
    RequestOutcomingDTO addNewRequest(Long userId, RequestIncomingDTO incomingDTO);

    List<RequestOutcomingDTO> getOwnRequests(Long userId);

    List<RequestOutcomingDTO> getAllRequests(Long userId);

    RequestOutcomingDTO getRequestById(Long userId, Long requestId);

    RequestOutcomingDTO update(Long authorId, RequestIncomingDTO userRequestDTO);
}
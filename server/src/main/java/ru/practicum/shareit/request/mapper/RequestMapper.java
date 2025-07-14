package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.RequestIncomingDTO;
import ru.practicum.shareit.request.dto.RequestOutcomingDTO;
import ru.practicum.shareit.request.model.AnswerToRequest;
import ru.practicum.shareit.request.model.Request;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static Request toModel(RequestIncomingDTO incomingDTO) {
        return new Request(
                incomingDTO.getDescription(),
                incomingDTO.getCreated(),
                incomingDTO.getAuthor()
        );
    }

    public static RequestOutcomingDTO toDto(Request request) {
        return new RequestOutcomingDTO(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                request.getAuthor(),
                new ArrayList<AnswerToRequest>()
        );
    }
}
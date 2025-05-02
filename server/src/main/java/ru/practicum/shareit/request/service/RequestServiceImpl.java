package ru.practicum.shareit.request.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemJPARepository;
import ru.practicum.shareit.request.dto.RequestIncomingDTO;
import ru.practicum.shareit.request.dto.RequestOutcomingDTO;
import ru.practicum.shareit.request.mapper.AnswerMapper;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.AnswerToRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestJPARepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserJPARepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestJPARepository requestJPARepository;
    private final UserJPARepository userJPARepository;
    private final ItemJPARepository itemJPARepository;

    @Autowired
    public RequestServiceImpl(RequestJPARepository requestJPARepository, UserJPARepository userJPARepository,
                              ItemJPARepository itemJPARepository) {
        this.requestJPARepository = requestJPARepository;
        this.userJPARepository = userJPARepository;
        this.itemJPARepository = itemJPARepository;
    }

    @Transactional
    public RequestOutcomingDTO addNewRequest(Long userId, RequestIncomingDTO incomingDTO) {
        if (!userJPARepository.existsById(userId)) {
            throw new NotFoundException("Юзер отсутствует");
        }
        incomingDTO.setAuthor(userJPARepository.findById(userId).get());
        incomingDTO.setCreated(LocalDateTime.now());
        Request request = RequestMapper.toModel(incomingDTO);
        request = requestJPARepository.save(request);
        return RequestMapper.toDto(request);
    }

    public List<RequestOutcomingDTO> getAllRequests(Long userId) {
        User user = userJPARepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с ID " + userId + " отсутствует."));
        Optional<List<Request>> requestList = requestJPARepository.getAllRequests();
        if (requestList.isEmpty()) {
            System.out.println("Запросы на вещи отсутствуют");
            return Collections.emptyList();
        }
        return requestList.get()
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RequestOutcomingDTO> getOwnRequests(Long userId) {
        User user = userJPARepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с ID " + userId + " отсутствует."));
        Optional<List<Request>> requestList = requestJPARepository.getRequestsById(userId);
        if (requestList.isEmpty()) {
            System.out.println("Пользователь с id " + userId + " не имеет запросов.");
            return Collections.emptyList();
        }
        List<RequestOutcomingDTO> result = requestList.get()
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        List<Long> requestIdList = result.stream()
                                         .map(req -> req.getId())
                                         .collect(Collectors.toList());
        if (itemJPARepository.getItemsByRequest(requestIdList).isPresent()) {
            List<Item> itemList = itemJPARepository.getItemsByRequest(requestIdList).get();
            result.forEach(r -> itemList.forEach(item -> {
                        if (r.getId().equals(item.getRequestId())) {
                            r.addToAnswerList(AnswerMapper.toAnswer(item));
                        }
                    }));
            return result;
        }
        return result;
    }

    public RequestOutcomingDTO getRequestById(Long userId, Long requestId) {
        User user = userJPARepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с ID " + userId + " отсутствует."));
        Optional<Request> request = requestJPARepository.getRequestById(requestId);
        if (request.isEmpty()) {
            System.out.println("Запросы на вещи отсутствуют");
            return new RequestOutcomingDTO();
        }
        RequestOutcomingDTO result = RequestMapper.toDto(request.get());
        List<Long> idList = Arrays.asList(result.getId());
        if (itemJPARepository.getItemsByRequest(idList).isPresent()) {
            List<Item> itemList = itemJPARepository.getItemsByRequest(idList).get();
            List<AnswerToRequest> items = itemList.stream()
                    .map(AnswerMapper::toAnswer)
                    .collect(Collectors.toList());
            result.setItems(items);
            return result;
        }
        return result;
    }
}
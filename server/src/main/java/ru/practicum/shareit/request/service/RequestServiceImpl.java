package ru.practicum.shareit.request.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.expection.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemJPARepository;
import ru.practicum.shareit.request.dto.RequestIncomingDTO;
import ru.practicum.shareit.request.dto.RequestOutcomingDTO;
import ru.practicum.shareit.request.mapper.AnswerMapper;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.AnswerToRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
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
    private final RequestRepository requestRepository;
    private final UserJPARepository userJPARepository;
    private final ItemJPARepository itemJPARepository;

    @Autowired
    public RequestServiceImpl(@Qualifier("jpaRepository") RequestRepository requestJPARepository, UserJPARepository userJPARepository,
                              ItemJPARepository itemJPARepository) {
        this.requestRepository = requestJPARepository;
        this.userJPARepository = userJPARepository;
        this.itemJPARepository = itemJPARepository;
    }

    @Transactional
    public RequestOutcomingDTO addNewRequest(Long userId, RequestIncomingDTO incomingDTO) {
        if (!userJPARepository.existsById(userId)) {
            throw new NotFoundException("Юзер отсутствует");
        }
        if (incomingDTO.getDescription() == null || incomingDTO.getDescription().isEmpty()) {
            throw new ValidationException("Description cannot be null or empty");
        }
        incomingDTO.setAuthor(userJPARepository.findById(userId).get());
        incomingDTO.setCreated(LocalDateTime.now());
        Request request = RequestMapper.toModel(incomingDTO);
        request = requestRepository.save(request);
        return RequestMapper.toDto(request);
    }

    public List<RequestOutcomingDTO> getAllRequests(Long userId) {
        User user = userJPARepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с ID " + userId + " отсутствует."));
        List<Request> requestList = requestRepository.findAll();
        if (requestList.isEmpty()) {
            System.out.println("Запросы на вещи отсутствуют");
            return Collections.emptyList();
        }
        return linkRequestToItem(requestList.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList()));
    }

    public List<RequestOutcomingDTO> getOwnRequests(Long userId) {
        User user = userJPARepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с ID " + userId + " отсутствует."));
        List<RequestOutcomingDTO> result = requestRepository.getRequestsByAuthorId(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            System.out.println("Пользователь с id " + userId + " не имеет запросов.");
            return Collections.emptyList();
        }
        return linkRequestToItem(result);
    }

    public RequestOutcomingDTO getRequestById(Long userId, Long requestId) {
        User user = userJPARepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер с ID " + userId + " отсутствует."));
        Optional<Request> request = requestRepository.getRequestById(requestId);
        if (request.isEmpty()) {
            System.out.println("Запросы на вещи отсутствуют");
            return new RequestOutcomingDTO();
        }
        RequestOutcomingDTO result = RequestMapper.toDto(request.get());
        List<Long> idList = Arrays.asList(result.getId());
        if (itemJPARepository.getItemsByRequest(idList).isEmpty()) {
            return result;
        }
        List<Item> itemList = itemJPARepository.getItemsByRequest(idList);
        List<AnswerToRequest> items = itemList.stream()
                .map(AnswerMapper::toAnswer)
                .collect(Collectors.toList());
        result.setItems(items);
        return result;
    }

    private List<RequestOutcomingDTO> linkRequestToItem(List<RequestOutcomingDTO> requestsList) {
        List<Long> requestIdList = requestsList.stream()
                .map(req -> req.getId())
                .collect(Collectors.toList());
        List<Item> itemList = itemJPARepository.getItemsByRequest(requestIdList);
        if (itemList.isEmpty()) {
            return requestsList;
        }
        requestsList.forEach(r -> itemList.forEach(item -> {
            if (r.getId().equals(item.getRequestId())) {
                r.addToAnswerList(AnswerMapper.toAnswer(item));
            }
        }));
        return requestsList;
    }
}

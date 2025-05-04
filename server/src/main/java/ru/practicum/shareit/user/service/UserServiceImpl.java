package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserJPARepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserJPARepository userJPARepository;

    @Autowired
    public UserServiceImpl(UserJPARepository userJPARepository) {
        this.userJPARepository = userJPARepository;
    }

    @Transactional
    public UserResponseDTO saveUser(UserRequestDTO userRequestDTO) {
        User user = UserMapper.toModel(userRequestDTO);
        user = userJPARepository.save(user);
        return UserMapper.toDto(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> userList = userJPARepository.findAll();
        if (userList == null || userList.size() == 0) throw new NotFoundException("Список юзеров пуст.");
        List<UserResponseDTO> dtoList = userList
                .stream()
                .map(user -> UserMapper.toDto(user))
                .collect(Collectors.toList());
        return dtoList;
    }

    public UserResponseDTO getUserById(Long userId) {
        if (!userJPARepository.existsById(userId)) {
            throw new NotFoundException("Юзер c Id " + userId + " отсутствует.");
        }
        return UserMapper.toDto(userJPARepository.findById(userId).get());
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO userRequestDTO) {
        if (userJPARepository.existsById(id)) {
            User user = userJPARepository.findById(id).get();
            for (String f : userRequestDTO.getNonNullFields().get()) {
                if (f.equals("email")) user.setEmail(userRequestDTO.getEmail());
                if (f.equals("name")) user.setName(userRequestDTO.getName());
            }
            user = userJPARepository.save(user);
            return UserMapper.toDto(user);
        } else {
            throw new NotFoundException("Юзер отсутствуют");
        }
    }

    @Transactional
    public void remove(Long id) {
        if (!userJPARepository.existsById(id)) {
            throw new NotFoundException("Юзер не найден с id = " + id);
        }
        userJPARepository.deleteById(id);
    }
}
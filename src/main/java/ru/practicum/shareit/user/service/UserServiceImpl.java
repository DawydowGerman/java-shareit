package ru.practicum.shareit.user.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO saveUser(UserRequestDTO userRequestDTO) {
        User user = UserMapper.toModel(userRequestDTO);
        user = userRepository.saveUser(user);
        return UserMapper.toDto(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        Optional<List<User>> userList = userRepository.findAll();
        if (userList.isPresent()) {
            List<UserResponseDTO> dtoList = userList.get()
                    .stream()
                    .map(user -> UserMapper.toDto(user))
                    .collect(Collectors.toList());
            return dtoList;
        } else throw new NotFoundException("Список юзеров пуст.");
    }

    public UserResponseDTO getUserById(Long userId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Юзер c Id " + userId + " отсутствует.");
        }
        return UserMapper.toDto(userRepository.getUserById(userId).get());
    }

    public UserResponseDTO update(Long id, UserRequestDTO userRequestDTO) {
        if (userRepository.isUserIdExists(id)) {
            User user0 = UserMapper.toModel(userRequestDTO);
            user0.setId(id);
            user0 = userRepository.update(user0);
            return UserMapper.toDto(user0);
        } else {
            throw new NotFoundException("Юзер отсутствуют");
        }
    }

    public void remove(Long id) {
        if (!userRepository.isUserIdExists(id)) {
            throw new NotFoundException("Юзер не найден с id = " + id);
        }
        userRepository.remove(id);
    }
}
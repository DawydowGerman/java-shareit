package ru.practicum.shareit.user.service;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.expection.ValidationException;
import ru.practicum.shareit.expection.InternalServerException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class UserServiceImpl {
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO saveUser(UserDTO userDto) {
        validateDto(userDto);
        User user = UserMapper.toModel(userDto);
        user = userRepository.saveUser(user);
        return UserMapper.toDto(user);
    }

    public List<UserDTO> getAllUsers() {
        Optional<List<User>> userList = userRepository.findAll();
        if (userList.isPresent()) {
            List<UserDTO> dtoList = userList.get()
                    .stream()
                    .map(user -> UserMapper.toDto(user))
                    .collect(Collectors.toList());
            return dtoList;
        } else throw new NotFoundException("Список юзеров пуст.");
    }

    public UserDTO getUserById(Long userId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Юзер c Id " + userId + " отсутствует.");
        }
        return UserMapper.toDto(userRepository.getUserById(userId).get());
    }

    public UserDTO update(Long id, UserDTO userDto) {
        if (userRepository.isUserIdExists(id)) {
            userDto.setId(id);
            if (userDto.getEmail() != null) validateDto(userDto);
            User user0 = UserMapper.toModel(userDto);
            user0 = userRepository.update(user0);
            return UserMapper.toDto(user0);
        } else {
            log.error("Ошибка при обновлении данных юзера");
            throw new NotFoundException("Юзер отсутствуют");
        }
    }

    public void remove(Long id) {
        if (!userRepository.isUserIdExists(id)) {
            log.error("Ошибка при удалении юзера");
            throw new NotFoundException("Юзер не найден с id = " + id);
        }
        userRepository.remove(id);
    }

    private void validateDto(UserDTO userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()
                || !userDto.getEmail().contains("@")) {
            log.error("Ошибка при добавлении юзера");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (userRepository.findAll().isPresent()) {
            userRepository.findAll().get()
                    .stream()
                    .forEach(user -> {
                        if (user.getEmail().equals(userDto.getEmail())) {
                            throw new InternalServerException("Этот имейл уже используется");
                        }
                    });
        }
    }
}

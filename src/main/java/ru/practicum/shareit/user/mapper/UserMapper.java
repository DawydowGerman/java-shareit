package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDTO;

public class UserMapper {
    public static User toModel(UserDTO userDTO) {
        return new User(
                userDTO.getId(),
                userDTO.getEmail(),
                userDTO.getName()
        );
    }

    public static UserDTO toDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}
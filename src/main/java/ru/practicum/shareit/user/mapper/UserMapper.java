package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class UserMapper {
    public static User toModel(UserRequestDTO userRequestDTO) {
        return new User(
                userRequestDTO.getEmail(),
                userRequestDTO.getName()
        );
    }

    public static UserResponseDTO toDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}
package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public UserResponseDTO saveUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        log.info("User's addition: {}", userRequestDTO);
        return userService.saveUser(userRequestDTO);
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        log.info("Get all of the users");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponseDTO getUserById(@PositiveOrZero @PathVariable Long userId) {
        log.info("Request of user by user ID: {}", userId);
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserResponseDTO update(@PositiveOrZero @PathVariable(name = "userId") Long id,
                          @RequestBody UserRequestDTO userRequestDTO) {
        log.info("User's update: {}", userRequestDTO);
        return userService.update(id, userRequestDTO);
    }

    @DeleteMapping("/{userId}")
    public void remove(@PositiveOrZero @PathVariable(name = "userId") Long id) {
        log.info("User's removal by ID: {}", id);
        userService.remove(id);
    }
}
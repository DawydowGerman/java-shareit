package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserRequestDTO;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        log.info("User's addition: {}", userRequestDTO);
        return userClient.saveUser(userRequestDTO);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all of the users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Request of user by user ID: {}", userId);
        return userClient.getUserById(userId);
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable(name = "userId") Long id,
                          @RequestBody UserRequestDTO userRequestDTO) {
        log.info("User's update: {}", userRequestDTO);
        return userClient.update(id, userRequestDTO);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void remove(@PathVariable(name = "userId") Long id) {
        log.info("User's removal by ID: {}", id);
        userClient.remove(id);
    }
}
package ru.practicum.shareit.user.model;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String email;
    private String name;

    public User(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}

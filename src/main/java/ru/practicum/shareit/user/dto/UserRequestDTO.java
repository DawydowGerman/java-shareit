package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequestDTO {
    @Email
    @NotNull
    private String email;
    @NotBlank
    private String name;

    public Optional<List<String>> getNonNullFields() {
        List<String> resultList = new ArrayList<>();
        Arrays.stream(this.getClass().getDeclaredFields())
                .forEach(f -> {
                    try {
                        if (f.get(this) != null) {
                            resultList.add(f.getName());
                        }
                    } catch (IllegalAccessException e) {
                        e.getMessage();
                    }
                });
        if (resultList.size() > 0) {
            return Optional.of(resultList);
        } return Optional.empty();
    }
}
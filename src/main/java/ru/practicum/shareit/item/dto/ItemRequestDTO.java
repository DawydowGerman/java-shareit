package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private Long requestId;

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
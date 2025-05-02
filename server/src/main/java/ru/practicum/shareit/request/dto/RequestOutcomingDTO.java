package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.request.model.AnswerToRequest;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestOutcomingDTO {
    private Long id;
    private String description;
    private LocalDateTime created;
    private User author;
    private List<AnswerToRequest> items;

    public RequestOutcomingDTO (Long id, String description,
                                LocalDateTime created, User author) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.author = author;
    }

    public void addToAnswerList(AnswerToRequest answer) {
        items.add(answer);
    }
}
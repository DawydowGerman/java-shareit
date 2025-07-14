package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;

    @AssertTrue(message = "Start must be before end")
    public boolean isStartBeforeEnd() {
        return start != null && end != null && start.isBefore(end);
    }

    @AssertFalse(message = "Start and end cannot be equal")
    public boolean isStartNotEqualEnd() {
        return start != null && end != null && start.equals(end);
    }

    @AssertFalse(message = "Start cannot be null")
    public boolean isStartNotEqualNull() {
        return start == null;
    }

    @AssertFalse(message = "End cannot be null")
    public boolean isEndNotEqualNull() {
        return end == null;
    }
}
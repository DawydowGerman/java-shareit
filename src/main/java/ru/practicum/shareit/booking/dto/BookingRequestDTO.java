package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingRequestDTO {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;

    @AssertTrue
    boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }

    @AssertFalse
    boolean isEndNotInPast() {
        return end.isBefore(LocalDateTime.now());
    }

    @AssertFalse
    boolean isStartNotInPast() {
        return start.isBefore(LocalDateTime.now());
    }

    @AssertFalse
    boolean isStartNotEqualEnd() {
        return start.equals(end);
    }

    @AssertFalse
    boolean isStartNotEqualNull() {
        return start.equals(null);
    }

    @AssertFalse
    boolean isEndNotEqualNull() {
        return end.equals(null);
    }
}
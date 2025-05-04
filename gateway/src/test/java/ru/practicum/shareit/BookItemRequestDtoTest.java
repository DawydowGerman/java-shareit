package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookItemRequestDtoTest {
    @Autowired
    private JacksonTester<BookItemRequestDto> jsonTester;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void failWhenStartAfterEnd() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 10, 0);
        BookItemRequestDto dto = new BookItemRequestDto(start, end, 1L);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting("message")
                .contains("Start must be before end");
    }

    @Test
    void failWhenStartEqualsEnd() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 10, 0);
        BookItemRequestDto dto = new BookItemRequestDto(time, time, 1L);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting("message")
                .contains("Start and end cannot be equal");
    }

    @Test
    void failOnlyEndNullValidationWhenEndIsNull() {
        BookItemRequestDto dto = new BookItemRequestDto(LocalDateTime.now(), null, 1L);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        boolean endNullViolationExists = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("endNotEqualNull"));

        assertThat(endNullViolationExists).isTrue();

        String message = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("endNotEqualNull"))
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("");

        assertThat(message).isEqualTo("End cannot be null");
    }

    @Test
    void failOnlyStartNullValidationWhenStartIsNull() {
        BookItemRequestDto dto = new BookItemRequestDto(null, LocalDateTime.now(), 1L);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        boolean startNullViolationExists = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("startNotEqualNull"));

        assertThat(startNullViolationExists).isTrue();

        String message = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("startNotEqualNull"))
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("");

        assertThat(message).isEqualTo("Start cannot be null");
    }
}
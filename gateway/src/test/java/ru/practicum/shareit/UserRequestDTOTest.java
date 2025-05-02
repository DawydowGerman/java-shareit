package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserRequestDTO;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserRequestDTOTest {

    @Autowired
    private JacksonTester<UserRequestDTO> jsonTester;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void whenAllFieldsValid() {
        UserRequestDTO dto = new UserRequestDTO("valid@example.com", "Valid Name");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenEmailNull() {
        UserRequestDTO dto = new UserRequestDTO(null, "Valid Name");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("must not be null");
    }

    @Test
    void whenEmailInvalid() {
        UserRequestDTO dto = new UserRequestDTO("invalid-email", "Valid Name");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("must be a well-formed email address");
    }

    @Test
    void whenNameBlank() {
        UserRequestDTO dto = new UserRequestDTO("valid@example.com", " ");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("must not be blank");
    }

    @Test
    void whenNameNull() {
        UserRequestDTO dto = new UserRequestDTO("valid@example.com", null);

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("must not be blank");
    }
}
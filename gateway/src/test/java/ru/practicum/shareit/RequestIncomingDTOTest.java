package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.practicum.shareit.request.dto.RequestIncomingDTO;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestIncomingDTOTest {

    @Autowired
    private JacksonTester<RequestIncomingDTO> jsonTester;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testBlank() {
        RequestIncomingDTO validDto = new RequestIncomingDTO("Valid description", null, null);
        Set<ConstraintViolation<RequestIncomingDTO>> violations = validator.validate(validDto);
        assertThat(violations).isEmpty();

        RequestIncomingDTO invalidDto = new RequestIncomingDTO("", null, null);
        violations = validator.validate(invalidDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("must not be blank");
    }
}

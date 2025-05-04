package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDTOTest {

    @Autowired
    private JacksonTester<ItemRequestDTO> jsonTester;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void nameIsBlankValidationFails() {
        ItemRequestDTO dto = new ItemRequestDTO();
        dto.setName("");
        dto.setDescription("Valid description");
        dto.setAvailable(true);

        Set<ConstraintViolation<ItemRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void descriptionIsBlankValidationFails() {
        ItemRequestDTO dto = new ItemRequestDTO();
        dto.setName("Valid name");
        dto.setDescription("");
        dto.setAvailable(true);

        Set<ConstraintViolation<ItemRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void availableIsNullValidationFails() {
        ItemRequestDTO dto = new ItemRequestDTO();
        dto.setName("Valid name");
        dto.setDescription("Valid description");
        dto.setAvailable(null);

        Set<ConstraintViolation<ItemRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("must not be null");
    }
}

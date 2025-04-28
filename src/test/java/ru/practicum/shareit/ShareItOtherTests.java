package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Transactional
@SpringBootTest(
        classes = ShareItApp.class,
//        properties = "jdbc.url=jdbc:h2:file:./db/filmorate",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(locations = {"classpath:application.properties"})
public class ShareItOtherTests {
    private final EntityManager em;
    private final UserServiceImpl service;

    @Test
    void testGetUserById() {
    //    UserRequestDTO userDto = new UserRequestDTO("some@email.com", "Peter");

        service.insertUser("some@email.com", "Peter");

        UserResponseDTO userDto0 = service.getUserById(service.getLastInsertId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto0.getEmail())
                .getSingleResult();

        assertThat(userDto0.getName(),equalTo(user.getName()));
    }
}

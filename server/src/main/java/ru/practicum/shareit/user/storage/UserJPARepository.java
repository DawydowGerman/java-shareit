package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJPARepository extends JpaRepository<User, Long> {
}
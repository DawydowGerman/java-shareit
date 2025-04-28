package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.model.User;

public interface UserJPARepository extends JpaRepository<User, Long> {
    @Modifying
    @Query(value = "INSERT INTO users (email, name) VALUES (:email, :name)",
            nativeQuery = true)
    void insertUser(@Param("email") String email, @Param("name") String name);

    // Then get the latest ID (H2 compatible)
    @Query(value = "SELECT id from users order by id desc limit 1", nativeQuery = true)
    Long getLastInsertId();
}
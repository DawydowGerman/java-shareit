package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestJPARepository extends JpaRepository<Request, Long> {
    @Query(value = "SELECT * from requests where author_id = :author_id order by id desc", nativeQuery = true)
    Optional<List<Request>> getRequestsById(@Param("author_id") Long authorId);

    @Query(value = "SELECT * from requests order by id desc", nativeQuery = true)
    List<Request> getAllRequests();

    @Query(value = "SELECT * from requests where id = :id", nativeQuery = true)
    Optional<Request> getRequestById(@Param("id") Long id);
}
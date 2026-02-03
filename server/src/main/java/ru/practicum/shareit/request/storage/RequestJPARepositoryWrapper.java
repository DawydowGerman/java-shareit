package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository("requestRepository")
public class RequestJPARepositoryWrapper implements RequestRepository {
    private final RequestJPARepository jpaRepository;

    public RequestJPARepositoryWrapper(RequestJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Request save(Request request) {
        return jpaRepository.save(request);
    }

    @Override
    public List<Request> findAll() {
        return jpaRepository.getAllRequests();
    }

    @Override
    public Optional<Request> getRequestById(Long id) {
        return jpaRepository.getRequestById(id);
    }

    @Override
    public List<Request> getRequestsByAuthorId(Long authorId) {
        return jpaRepository.getRequestsByAuthorId(authorId);
    }

    @Override
    public Request update(Request newUser) {
        return jpaRepository.save(newUser);
    }

    @Override
    public boolean isRequestIdExists(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

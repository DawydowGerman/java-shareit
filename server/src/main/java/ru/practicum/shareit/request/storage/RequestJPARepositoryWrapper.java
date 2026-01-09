package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository("requestRepository")
public class RequestJPARepositoryWrapper implements RequestRepository {
    private final RequestRepository jpaRepository;

    public RequestJPARepositoryWrapper(RequestRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Request save(Request request) {
        return jpaRepository.save(request);
    }

    @Override
    public List<Request> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Request update(Request newUser){
        return jpaRepository.save(newUser);
    }

    @Override
    public Optional<Request> getRequestById(Long id) {
        return jpaRepository.getRequestById(id);
    }

    @Override
    public void remove(Long id) {
        jpaRepository.remove(id);
    }
}

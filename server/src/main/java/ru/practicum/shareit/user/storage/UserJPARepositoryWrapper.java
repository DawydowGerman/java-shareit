package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository("jpaRepository")
public class UserJPARepositoryWrapper implements UserRepository {
    private final UserJPARepository jpaRepository;

    public UserJPARepositoryWrapper(UserJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User saveUser(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public boolean isUserIdExists(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public User update(User newUser){
        return jpaRepository.save(newUser);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void remove(Long id) {
        jpaRepository.deleteById(id);
    }
}

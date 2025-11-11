package backend.src.main.java.cl.app.infrastructure.persistence.jpa.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import backend.src.main.java.cl.app.domain.model.User;
import backend.src.main.java.cl.app.domain.port.UserRepository;
import backend.src.main.java.cl.app.infrastructure.persistence.jpa.entity.UserJpa;
import backend.src.main.java.cl.app.infrastructure.persistence.jpa.repo.UserJpaRepository;

@Component
public class UserRepositoryJpa implements UserRepository {

    private final UserJpaRepository repo;

    public UserRepositoryJpa(UserJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public User save(User user) {
        String passwordHash;


        if (user.id() != null) {
            passwordHash = repo.findById(user.id())
                    .map(UserJpa::getPasswordHash)
                    .orElse("");
        } else {

            passwordHash = "";
        }

        UserJpa jpa = UserJpa.fromDomain(user, passwordHash);
        UserJpa saved = repo.save(jpa);
        return saved.toDomain();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email)
                .map(UserJpa::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repo.findById(id)
                .map(UserJpa::toDomain);
    }

    @Override
    public void delete(Long userId) {
        repo.deleteById(userId);
    }
}
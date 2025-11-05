package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import cl.venegas.buses_api.domain.model.User;
import cl.venegas.buses_api.domain.port.UserRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.UserJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.UserJpaRepository;

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